/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package book.receiver;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.*;

import com.sun.xml.messaging.soap.server.SAAJServlet;

/**
 * Sample servlet that receives messages.
 *
 * @author Krishna Meduri (krishna.meduri@sun.com)
 */

public class ReceivingServlet extends SAAJServlet
{

    static Logger
        logger = Logger.getLogger("Samples/Book");

    // This is the application code for handling the message.. Once the
    // message is received the application can retrieve the soap part, the
    // attachment part if there are any, or any other information from the
    // message.

    public SOAPMessage onMessage(SOAPMessage message) {
        System.out.println("On message called in receiving servlet");
        try {
            System.out.println("Here's the message: ");
            message.writeTo(System.out);

	    SOAPHeader header = message.getSOAPHeader();
	    SOAPBody body = message.getSOAPBody();

	    Iterator headerElems = header.examineMustUnderstandHeaderElements(
						"http://saaj.sample/receiver");

	    while(headerElems.hasNext()) {
		SOAPHeaderElement elem = (SOAPHeaderElement) headerElems.next();
                String actor = elem.getActor();
                boolean mu = elem.getMustUnderstand();
                Iterator elemChildren = elem.getChildElements();

		System.out.println("**************");
		System.out.println("actor and mu are " + actor + " " + mu);
                while(elemChildren.hasNext()) {
                    SOAPElement elem1 = (SOAPElement)elemChildren.next();
                    org.w3c.dom.Node child = elem1.getFirstChild();
                    String childValue = child.getNodeValue();
		    System.out.println("childValue is " + childValue);
		}
	    }

            return message;

        } catch(Exception e) {
            logger.log(
                Level.SEVERE,
                "Error in processing or replying to a message", 
                e);
            return null;
        }
    }
}

