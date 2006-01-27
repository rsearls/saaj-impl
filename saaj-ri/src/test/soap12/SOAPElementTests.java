/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */
package soap12;

import javax.xml.soap.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.*;

import java.io.*;

import junit.framework.TestCase;


public class SOAPElementTests extends TestCase {

    public SOAPElementTests(String name) throws Exception {
        super(name);
    }

    public void testSetEncodingStyle1() throws Exception {
        MessageFactory mFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage msg = mFactory.createMessage();
        SOAPBody body = msg.getSOAPBody();
        SOAPBodyElement bodyElement =
            body.addBodyElement(new QName("some-uri", "content", "p"));
        bodyElement.setEncodingStyle("http://example.com/encoding");
        assertEquals(
            bodyElement.getAttributeValue(
                new QName(
                    SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE,
                    "encodingStyle")),
            "http://example.com/encoding");
    }

    /**
     * Testcase for CR ID 6213337
     */
    public void testSetEncodingStyle2() throws Exception {
        MessageFactory mFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPFactory sFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage msg = mFactory.createMessage();
        SOAPBody body = msg.getSOAPBody();
        SOAPElement bodyElement = sFactory.createElement("content", "p", "some-uri");
        bodyElement.setEncodingStyle("http://example.com/encoding");
        SOAPBodyElement addedElement = (SOAPBodyElement) body.addChildElement(bodyElement);
        assertEquals(
            addedElement.getAttributeValue(
                new QName(
                    SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE,
                    "encodingStyle")),
            "http://example.com/encoding");
    }

    /**
     * Testcase for CR ID 6213350
     */
    public void testGetEncodingStyle() throws Exception {
        String xml =
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Header/><SOAP-ENV:Body><p:content SOAP-ENV:encodingStyle=\"http://example.com/encoding\" xmlns:p=\"some-uri\"/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        MessageFactory mFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage msg = mFactory.createMessage();
        SOAPPart soapPart = msg.getSOAPPart();
        soapPart.setContent(new StreamSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
        SOAPBodyElement element =
            (SOAPBodyElement) msg.getSOAPBody().getChildElements().next();
        assertNotNull(element.getEncodingStyle());
    }
}
