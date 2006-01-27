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
/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package soap;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;

public class QNameTest extends TestCase {

	public QNameTest(String name) {
	        super(name);
    	}

	public void testAddDetailEntry() throws Exception {
        	SOAPFactory soapFactory = SOAPFactory.newInstance();
	        QName name = new QName(
        	        "http://faultservice.org/types",
                	"BasicFaultElement",
	                "ns0");
        	QName name2 = new QName(
                	"http://faultservice.org/types",
	                "AdditionalElement",
        	        "ns0");
	        Detail detail = soapFactory.createDetail();
        	DetailEntry entry = detail.addDetailEntry(name);

	        SOAPElement child = entry.addChildElement("Project");
        	entry.addChildElement("Mi", "ns0", "http://faultservice.org/types");
	        entry.addChildElement("Chiamo", "ns0", "http://faultservice.org/types");
 		entry.addChildElement("JAXRPC", "ns0", "http://faultservice.org/types");
	        entry.addChildElement("SI", "ns0", "http://faultservice.org/types");
        	entry.addChildElement(
		            "Implementation",
		            "ns0",
		            "http://faultservice.org/types");
	        child.addTextNode("Il mio nome e JAXRPC SI");
        	entry = detail.addDetailEntry(name2);

	        child = entry.addChildElement("Project");
        	child.addTextNode("2 text");

	        org.w3c.dom.Node firstChild = detail.getFirstChild();
        	assertTrue(firstChild != null);
	        org.w3c.dom.Node secondChild = firstChild.getNextSibling();
        	assertTrue(secondChild != null);
    	}

    	public void testAddFault2() throws Exception {
        	String testDoc =
	 	"<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'\n"
                + " xmlns:ns1='http://example.com/wsdl'>\n"
                + "  <env:Body>\n"
                + "    <ns1:sayHello>\n"
                + "      <String_1>Duke!</String_1>\n"
                + "    </ns1:sayHello>\n"
                + "  </env:Body>\n"
                + "</env:Envelope>\n";

        	byte[] testDocBytes = testDoc.getBytes("UTF-8");
        	ByteArrayInputStream bais = new ByteArrayInputStream(testDocBytes);
	        StreamSource strSource = new StreamSource(bais);
        	MessageFactory mf = MessageFactory.newInstance();
	        SOAPMessage sm = mf.createMessage();
        	SOAPPart sp = sm.getSOAPPart();
	        sp.setContent(strSource);
                SOAPEnvelope envelope = sp.getEnvelope();
        	String prefix = envelope.getElementName().getPrefix();
        	String uri = envelope.getElementName().getURI();
                SOAPBody body = envelope.getBody();
                QName name = new QName(uri,"Server", prefix);
                try {
                  SOAPFault fault = body.addFault(name, 
                                   "This is a Server fault"); 
		  assertTrue(fault != null);
                }catch (Exception e) {
                   e.printStackTrace();
                   fail();
              }
    	}

    	public void testAddFault3() throws Exception {
        	String testDoc =
	 	"<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'\n"
                + " xmlns:ns1='http://example.com/wsdl'>\n"
                + "  <env:Body>\n"
                + "    <ns1:sayHello>\n"
                + "      <String_1>Duke!</String_1>\n"
                + "    </ns1:sayHello>\n"
                + "  </env:Body>\n"
                + "</env:Envelope>\n";

        	byte[] testDocBytes = testDoc.getBytes("UTF-8");
        	ByteArrayInputStream bais = new ByteArrayInputStream(testDocBytes);
	        StreamSource strSource = new StreamSource(bais);
        	MessageFactory mf = MessageFactory.newInstance();
	        SOAPMessage sm = mf.createMessage();
        	SOAPPart sp = sm.getSOAPPart();
	        sp.setContent(strSource);
                SOAPEnvelope envelope = sp.getEnvelope();
        	String prefix = envelope.getElementName().getPrefix();
        	String uri = envelope.getElementName().getURI();
                SOAPBody body = envelope.getBody();
                QName name = new QName(uri,"Server", prefix);
                Locale l = new Locale("en", "US");
		try {
                  SOAPFault fault = body.addFault(name, 
                                   "This is a Server fault", l); 
		  assertTrue(fault != null);
                }catch (Exception e) {
                   e.printStackTrace();
                   fail();
              }
    	}

	public void testAddBodyElement() throws Exception {
		MessageFactory msgFactory = MessageFactory.newInstance();

        	SOAPMessage msg = msgFactory.createMessage();

	        SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();

        	SOAPHeader hdr = envelope.getHeader();
	        SOAPBody bdy = envelope.getBody();

        	SOAPHeaderElement transaction
            		= hdr.addHeaderElement(envelope.createName("Transaction", "t", "some-uri"));

	        transaction.setMustUnderstand(true);
        	transaction.addTextNode("5");

	        SOAPBodyElement ltp
        	    	= bdy.addBodyElement(new QName("some-other-uri", "GetLastTradePrice", "m"));
		//msg.writeTo(System.out);
		assertTrue(ltp.getElementName().getLocalName().equals("GetLastTradePrice"));
    	}

	public void testAddChildElement() throws Exception {
		SOAPElementFactory factory = SOAPElementFactory.newInstance();
	        SOAPElement element = factory.create("testElement");

        	element.addChildElement(new QName("uri", "child", "prefix"));

	        Iterator eachChild = element.getChildElements(new QName("uri", "child", "prefix"));
        	assertTrue("First element is there", eachChild.hasNext());
	        SOAPElement child = (SOAPElement) eachChild.next();
        	assertEquals("prefix:child", child.getTagName());
	        assertFalse("Extra elements", eachChild.hasNext());
	}

	public void testAddAttribute() throws Exception {
		SOAPElementFactory factory = SOAPElementFactory.newInstance();
	        SOAPElement element = factory.create("testElement");

        	QName originalAttributeName = new QName("unqualifiedName");
	        String originalAttributeValue = "aValue";
        	element.addAttribute(originalAttributeName, originalAttributeValue);

	        Name theAttributeName = null;
        	String theAttributeValue = null;
	        int count = 0;
        	for (Iterator eachAttribute = element.getAllAttributes();
            		eachAttribute.hasNext();
            	    ) {
            		theAttributeName = (Name) eachAttribute.next();
	            	theAttributeValue = element.getAttributeValue(theAttributeName);

            		++count;
            		assertEquals(1, count);
        	      }
		
		assertEquals(
            		"Qualified names of attributes must match",
            		ElementImpl.getQualifiedName(originalAttributeName),
            		theAttributeName.getQualifiedName());
        	assertEquals(
            		"Attribute values must match",
            		originalAttributeValue,
            		theAttributeValue);

	}

	public void testGetAttributeValue() throws Exception {
		SOAPElementFactory factory = SOAPElementFactory.newInstance();
	        SOAPElement element = factory.create("testElement");

        	QName originalAttributeName = new QName("unqualifiedName");
	        String originalAttributeValue = "aValue";
        	element.addAttribute(originalAttributeName, originalAttributeValue);
        	assertTrue(originalAttributeValue.equals(element.getAttributeValue(originalAttributeName)));
	        element.removeAttribute(originalAttributeName);

        	Name theAttributeName = null;
	        String theAttributeValue = null;
        	String unexpectedAttributelist = "";
	        int count = 0;
       	 	for (Iterator eachAttribute = element.getAllAttributes();
            		eachAttribute.hasNext();) {
            		
			theAttributeName = (Name) eachAttribute.next();
            		theAttributeValue = element.getAttributeValue(theAttributeName);

            		++count;
			unexpectedAttributelist += theAttributeName.getQualifiedName()
                				+ " = "
                				+ theAttributeValue
                				+ "\n";
        	}
        	
		assertEquals(
            		"Unexpected attributes:\n" + unexpectedAttributelist,
            		0,
            		count);

	        theAttributeValue = element.getAttributeValue(originalAttributeName);

        	assertTrue(
            		"Should have been null but was: " + "\"" + theAttributeValue + "\"",            null == theAttributeValue);
    	}
	
	public void testCreateElement() throws Exception {
   		SOAPFactory sFactory = SOAPFactory.newInstance();
		MessageFactory msgFactory = MessageFactory.newInstance();
		SOAPEnvelope envelope;
		try {
            		SOAPMessage msg = msgFactory.createMessage();
	            	envelope = msg.getSOAPPart().getEnvelope();

        	    	SOAPHeader hdr = envelope.getHeader();
            		SOAPBody bdy = envelope.getBody();
	           	SOAPElement elem = sFactory.createElement(new QName("localname"));
        	   	bdy.addChildElement(elem);

            	} catch (Exception e) {
            		e.printStackTrace();
            		fail("No exception should be thrown" + e.getMessage());
                  }
	}

	public void testSetFaultCode() throws Exception {
		MessageFactory msgFactory = MessageFactory.newInstance();
	        SOAPMessage msg = msgFactory.createMessage();
        	SOAPPart soapPart = msg.getSOAPPart();
	        SOAPEnvelope envelope = soapPart.getEnvelope();
        	SOAPHeader hdr = envelope.getHeader();
	        SOAPBody body = envelope.getBody();
	
        	SOAPFault sf = body.addFault();
	        String faultCodeLocalName = "Client2";
        	String faultCodePrefix = "fcp";
	        String faultCodeUri = "http://test/fault/code";
        	QName faultCodeName =
            		new QName(
                		faultCodeUri,
                		faultCodeLocalName,
                		faultCodePrefix);
        	sf.setFaultCode(faultCodeName);
	        sf.setFaultString("This is the fault string");
        	QName faultCode = sf.getFaultCodeAsQName();
	        assertEquals(faultCodePrefix, faultCode.getPrefix());
        	assertEquals(faultCodeUri, faultCode.getNamespaceURI());
	        assertEquals(faultCodeLocalName, faultCode.getLocalPart());
	}

	public void testAddHeaderElement() throws Exception {
        	SOAPMessage message = MessageFactory.newInstance().createMessage();
	        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        	SOAPHeader header = envelope.getHeader();

	        QName name1 = new QName("foo", "firstElement", "");
        	QName name2 = new QName("foo", "secondElement", "");

	        SOAPHeaderElement element1 = header.addHeaderElement(name1);
	        element1.setActor("theActor");

	        SOAPHeaderElement element2 = header.addHeaderElement(name2);
        	element2.setActor("theActor");

	        Iterator eachElement = header.extractHeaderElements("theActor");
	
        	assertTrue("First element is there", eachElement.hasNext());
	        SOAPHeaderElement outElement1 = (SOAPHeaderElement) eachElement.next();
	        assertTrue("Second element is there", eachElement.hasNext());
		SOAPHeaderElement outElement2 = (SOAPHeaderElement) eachElement.next();
        	assertFalse("No more elements", eachElement.hasNext());

        	assertEquals("First element is correct", element1, outElement1);
	        assertEquals("Second element is correct", element2, outElement2);
    	}
}

