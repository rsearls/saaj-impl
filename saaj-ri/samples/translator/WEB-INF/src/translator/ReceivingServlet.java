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

/*
 * $Id: ReceivingServlet.java,v 1.5 2009-01-17 00:39:49 ramapulavarthi Exp $
 * $Revision: 1.5 $s
 * $Date: 2009-01-17 00:39:49 $
 */


package translator;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.xml.soap.*;

/**
 * Sample servlet that receives messages containing text to be translated,
 * does the translation, and sends back a message with translations as
 * attachments -or- in SOAPBody of the reply message.
 *
 * @author Manveen Kaur (manveen.kaur@sun.com)
 *
 */

public class ReceivingServlet extends HttpServlet {

    private static String NS_PREFIX = "saaj";
    private static String
    NS_URI = "http://java.sun.com/saaj/samples/translation";
    private static Logger logger = Logger.getLogger("Samples/Translator");

    private String french = "";
    private String german = "";
    private String italian = "";


    static MessageFactory msgFactory = null;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        try {
            // Initialize it to the default.
            msgFactory = MessageFactory.newInstance();
        } catch (SOAPException ex) {
            throw new ServletException("Unable to create message factory"
                + ex.getMessage());
        }
    }

    public void doPost( HttpServletRequest req,
            HttpServletResponse resp)
    throws ServletException, IOException {

    try {
        // Get all the headers from the HTTP request.
        MimeHeaders headers = getHeaders(req);

        // Get the body of the HTTP request.
        InputStream is = req.getInputStream();

        // Now internalize the contents of a HTTP request and
        // create a SOAPMessage
        SOAPMessage msg = msgFactory.createMessage(headers, is);

        SOAPMessage reply = null;

        // There are no replies in case of an OnewayListener.
        reply = onMessage(msg);

        if (reply != null) {

        // Need to saveChanges 'cos we're going to use the
        // MimeHeaders to set HTTP response information. These
        // MimeHeaders are generated as part of the save.

        if (reply.saveRequired()) {
            reply.saveChanges();
        }

        resp.setStatus(HttpServletResponse.SC_OK);

        putHeaders(reply.getMimeHeaders(), resp);

        // Write out the message on the response stream.
        OutputStream os = resp.getOutputStream();
        reply.writeTo(os);

        os.flush();

        } else
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

    } catch (Exception ex) {
        throw new ServletException("POST failed "+ex.getMessage());
    }
    }

    static MimeHeaders getHeaders(HttpServletRequest req) {

    Enumeration enumeration = req.getHeaderNames();
    MimeHeaders headers = new MimeHeaders();

    while (enumeration.hasMoreElements()) {
        String headerName = (String)enumeration.nextElement();
        String headerValue = req.getHeader(headerName);

        StringTokenizer values = new StringTokenizer(headerValue, ",");
        while (values.hasMoreTokens())
        headers.addHeader(headerName, values.nextToken().trim());
    }

    return headers;
    }

    static void putHeaders(MimeHeaders headers, HttpServletResponse res) {

    Iterator it = headers.getAllHeaders();
    while (it.hasNext()) {
        MimeHeader header = (MimeHeader)it.next();

        String[] values = headers.getHeader(header.getName());
        if (values.length == 1)
        res.setHeader(header.getName(), header.getValue());
        else {
        StringBuffer concat = new StringBuffer();
        int i = 0;
        while (i < values.length) {
            if (i != 0)
            concat.append(',');
            concat.append(values[i++]);
        }

        res.setHeader(header.getName(),
        concat.toString());
            }
        }
    }

    // This is the application code for handling the message.. Once the
    // message is received the application can retrieve the soap part, the
    // attachment part if there are any, or any other information from the
    // message.

    public SOAPMessage onMessage(SOAPMessage message) {
        SOAPMessage msg = null;

        try {

            System.out.println("\n************** REQUEST ***************\n");

            message.writeTo(System.out);
            FileOutputStream os = new FileOutputStream("request.msg");
            message.writeTo(os);
            os.close();

            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            SOAPHeader header = envelope.getHeader();
            SOAPBody body = envelope.getBody();

            // Extracting Proxy information from SOAPHeader.
            String host = extract(envelope, header, "ProxyHost");

            String port = extract(envelope, header, "ProxyPort");

            String translationAs = extract(envelope, header, "TranslationAs");

            // Extracting text to be translated from SOAPBody.
            String text = extract(envelope, body, "Text");

            TranslationService ts;

            if ((host == null) || (host.equals("")) ||
                (port == null) || (port.equals(""))) {
                ts = new TranslationService();
            } else {
                ts = new TranslationService(host,port);
            }

            // Translate using the Translation Web-Service.
            german = ts.translate(text, TranslationService.ENGLISH_TO_GERMAN);
            french = ts.translate(text, TranslationService.ENGLISH_TO_FRENCH);
            italian = ts.translate(text, TranslationService.ENGLISH_TO_ITALIAN);

            // Create reply message
            msg = msgFactory.createMessage();

            if (translationAs.equals("body")) {
                addInSOAPBody(msg);
            } else {
                addAsAttachments(msg);
            }

            if (msg.saveRequired())
                msg.saveChanges();

        } catch(Exception e) {
            logger.log(
                Level.SEVERE,
                "Error in processing or replying to a message", 
                e);
        }

        return msg;
    }

    private void addInSOAPBody(SOAPMessage msg) {
        try {

            SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
            SOAPBody body = envelope.getBody();

            // Adding the translated text to SOAPBody.
            body.addBodyElement(envelope.createName("FrenchText",
            NS_PREFIX, NS_URI)).addTextNode(french);

            body.addBodyElement(envelope.createName("GermanText",
            NS_PREFIX, NS_URI)).addTextNode(german);

            body.addBodyElement(envelope.createName( "ItalianText",
            NS_PREFIX, NS_URI)).addTextNode(italian);

        } catch(Exception e) {
            logger.log(
                Level.SEVERE,
                "Error in adding translation to the body", 
                e);
        }
    }

    private void addAsAttachments(SOAPMessage msg) {
        // Adding the translations as attachments.
        try {

            SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();

            AttachmentPart ap_french =
            msg.createAttachmentPart(french,
            "text/plain; charset=ISO-8859-1");
            msg.addAttachmentPart(ap_french);

            AttachmentPart ap_german =
            msg.createAttachmentPart(german,
            "text/plain; charset=ISO-8859-1");
            msg.addAttachmentPart(ap_german);

            AttachmentPart ap_italian =
            msg.createAttachmentPart(italian,
            "text/plain; charset=ISO-8859-1");
            msg.addAttachmentPart(ap_italian);

        } catch(Exception e) {
            logger.log(
                Level.SEVERE,
                "Error in adding translations as attachments",
                e);
        }
    }

    // extract the value of the first child element under element
    // with this localname
    private String extract(SOAPEnvelope envelope, SOAPElement element, String localname)
    throws SOAPException {

        Iterator it = element.getChildElements(
        envelope.createName(localname, NS_PREFIX, NS_URI));

        if( it.hasNext()) {
            SOAPElement e = (SOAPElement) it.next();
            return e.getValue();
        }
        logger.severe("Could not extract " + localname + " from message");
        return null;
    }

}



