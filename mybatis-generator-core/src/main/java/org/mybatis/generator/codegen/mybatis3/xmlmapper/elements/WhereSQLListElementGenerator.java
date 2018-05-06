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

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.Iterator;
import java.util.List;

/**
 * Created by wangcong on 2018-5-1.
 * 增加动态语句
 */
public class WhereSQLListElementGenerator extends AbstractXmlElementGenerator {

    public WhereSQLListElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {

        XmlElement answer = new XmlElement("sql"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", //$NON-NLS-1$
                introspectedTable.getWhereSQLListId()));

        XmlElement valuesWhereElement = new XmlElement("where"); //$NON-NLS-1$
        answer.addElement(valuesWhereElement);

        StringBuilder sb = new StringBuilder();

        List<IntrospectedColumn> list = introspectedTable
                .getNonBLOBColumns();

        for (int i = 0; i < list.size(); i++) {
            IntrospectedColumn introspectedColumn = list.get(i);
            XmlElement insertNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$

            insertNotNullElement.addAttribute(new Attribute(
                    "test", sb.toString())); //$NON-NLS-1$

            sb.setLength(0);
            sb.append("and ");
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            insertNotNullElement.addElement(new TextElement(sb.toString()));
            valuesWhereElement.addElement(insertNotNullElement);
        }

        if (context.getPlugins().sqlMapWhereSQLListElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
