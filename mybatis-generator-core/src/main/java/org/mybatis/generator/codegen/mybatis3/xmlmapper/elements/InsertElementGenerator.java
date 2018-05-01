/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class InsertElementGenerator extends AbstractXmlElementGenerator {

    private boolean isSimple;

    public InsertElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getInsertStatementId())); //$NON-NLS-1$

        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(
                    introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules()
                    .calculateAllFieldsClass();
        }

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                "list"));

        context.getCommentGenerator().addComment(answer);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = introspectedTable
                    .getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute(
                            "useGeneratedKeys", "true")); //$NON-NLS-1$ //$NON-NLS-2$
                    answer.addAttribute(new Attribute(
                            "keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
                    answer.addAttribute(new Attribute(
                            "keyColumn", introspectedColumn.getActualColumnName())); //$NON-NLS-1$
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder insertClause = new StringBuilder();

        insertClause.append("insert into "); //$NON-NLS-1$
        insertClause.append(introspectedTable
                .getFullyQualifiedTableNameAtRuntime());
        insertClause.append(" ("); //$NON-NLS-1$

        StringBuilder valuesClause = new StringBuilder();
        valuesClause.append(" (");

        //List<String> valuesClauses = new ArrayList<String>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);
            if (introspectedColumn.isSequenceColumn()
                    || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                // if it is a sequence column, it is not optional
                // This is required for MyBatis3 because MyBatis3 parses
                // and calculates the SQL before executing the selectKey

                // if it is primitive, we cannot do a null check
                insertClause.setLength(0);
                insertClause.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                insertClause.append(',');

                valuesClause.setLength(0);
                valuesClause.append(MyBatis3FormattingUtilities
                        .getParameterClause(introspectedColumn));
                valuesClause.append(',');

                continue;
            }

            insertClause.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));

            valuesClause.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn).replace("#{", "#{item."));
            if (i + 1 < columns.size()) {
                insertClause.append(", "); //$NON-NLS-1$
                valuesClause.append(", "); //$NON-NLS-1$
            }

//            if (valuesClause.length() > 80) {
//                answer.addElement(new TextElement(insertClause.toString()));
//                insertClause.setLength(0);
//                OutputUtilities.xmlIndent(insertClause, 1);
//
//                valuesClauses.add(valuesClause.toString());
//                valuesClause.setLength(0);
//                OutputUtilities.xmlIndent(valuesClause, 1);
//            }
        }

        insertClause.append(')').append(" values ");
        answer.addElement(new TextElement(insertClause.toString()));

        valuesClause.append(')');

        XmlElement valuesTrimElement = new XmlElement("foreach"); //$NON-NLS-1$
        valuesTrimElement.addAttribute(new Attribute("collection", "list")); //$NON-NLS-1$
        // $NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("item", "item")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("separator", ",")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addElement(new TextElement(valuesClause.toString()));
        answer.addElement(valuesTrimElement);
//        for (String clause : valuesClauses) {
//            answer.addElement(new TextElement(clause));
//        }

        if (context.getPlugins().sqlMapInsertElementGenerated(answer,
                introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
