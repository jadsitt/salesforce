/** ========================================================================= *
 * Copyright (C)  2017, 2018 Salesforce Inc ( http://www.salesforce.com/      *
 *                            All rights reserved.                            *
 *                                                                            *
 *  @author     Stephan H. Wissel (stw) <swissel@salesforce.com>              *
 *                                       @notessensei                         *
 * @version     1.0                                                           *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== *
 */
package net.wissel.tool.findStrings;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author swissel
 *
 */
public class ReportRendererXML implements ReportRenderer {

    /**
     * @see net.wissel.tool.findStrings.ReportRenderer#render(java.util.Map,
     *      java.util.Map, java.io.PrintStream)
     */
    @Override
    public void render(Map<String, Set<String>> results, Map<String, String> keys, PrintStream out) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            final Document doc = docBuilder.newDocument();
            final Element rootElement = doc.createElement("result");
            final Element keyRoot = doc.createElement("keys");
            keys.forEach((k,v) -> {
               Element key = doc.createElement("key");        
               key.appendChild(doc.createTextNode(v));
               keyRoot.appendChild(key);
            });
            final Element hitRoot = doc.createElement("hits");
            results.forEach((key,list) -> {
                list.forEach(hit -> {
                    Element hitE = doc.createElement("hit");
                    hitE.setAttribute("key", key);
                    hitE.setAttribute("file", hit);
                    hitRoot.appendChild(hitE);
                });
            });

            // Finally the root
            rootElement.appendChild(keyRoot);
            rootElement.appendChild(hitRoot);
            doc.appendChild(rootElement);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
