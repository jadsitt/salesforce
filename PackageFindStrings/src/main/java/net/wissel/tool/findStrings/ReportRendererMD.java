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

/**
 * @author swissel
 *
 */
public class ReportRendererMD implements ReportRenderer {

    /**
     * @see net.wissel.tool.findStrings.ReportRenderer#render(java.util.Map, java.util.Map, java.io.PrintStream)
     */
    @Override
    public void render(Map<String, Set<String>> results, Map<String, String> keys, PrintStream out) {
        out.println();
        out.println("# Scan Results");
        out.println();
        out.println("## Strings found in files");
        out.println();
        keys.forEach((key, printval) -> {

            if (results.containsKey(key)) {
                out.println("### " + printval);
                out.println();
                results.get(key).forEach(f -> {
                    out.println("- " + f);
                });
                out.println();
            }

        });

        out.println("## Strings not found");
        out.println();
        keys.forEach((k, v) -> {
            if (!results.containsKey(k)) {
                out.println("- " + v);
            }
        });
        out.flush();
        out.close();

    }

}
