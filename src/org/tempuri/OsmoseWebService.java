
package org.tempuri;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "OsmoseWebService", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://localhost:58000/OsmoseWebService.svc?singleWsdl")
public class OsmoseWebService
    extends Service
{

    private final static URL OSMOSEWEBSERVICE_WSDL_LOCATION;
    private final static WebServiceException OSMOSEWEBSERVICE_EXCEPTION;
    private final static QName OSMOSEWEBSERVICE_QNAME = new QName("http://tempuri.org/", "OsmoseWebService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:58000/OsmoseWebService.svc?singleWsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        OSMOSEWEBSERVICE_WSDL_LOCATION = url;
        OSMOSEWEBSERVICE_EXCEPTION = e;
    }

    public OsmoseWebService() {
        super(__getWsdlLocation(), OSMOSEWEBSERVICE_QNAME);
    }

    public OsmoseWebService(WebServiceFeature... features) {
        super(__getWsdlLocation(), OSMOSEWEBSERVICE_QNAME, features);
    }

    public OsmoseWebService(URL wsdlLocation) {
        super(wsdlLocation, OSMOSEWEBSERVICE_QNAME);
    }

    public OsmoseWebService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, OSMOSEWEBSERVICE_QNAME, features);
    }

    public OsmoseWebService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public OsmoseWebService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns IOsmoseWebService
     */
    @WebEndpoint(name = "BasicHttpBinding_IOsmoseWebService")
    public IOsmoseWebService getBasicHttpBindingIOsmoseWebService() {
        return super.getPort(new QName("http://tempuri.org/", "BasicHttpBinding_IOsmoseWebService"), IOsmoseWebService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IOsmoseWebService
     */
    @WebEndpoint(name = "BasicHttpBinding_IOsmoseWebService")
    public IOsmoseWebService getBasicHttpBindingIOsmoseWebService(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "BasicHttpBinding_IOsmoseWebService"), IOsmoseWebService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (OSMOSEWEBSERVICE_EXCEPTION!= null) {
            throw OSMOSEWEBSERVICE_EXCEPTION;
        }
        return OSMOSEWEBSERVICE_WSDL_LOCATION;
    }

}
