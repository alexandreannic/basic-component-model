package fr.upmc.components.cvm.config;

// Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
// 
// Jacques.Malenfant@lip6.fr
// 
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
// 
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
// 
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
// 
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
// 
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The class <code>ConfigurationFileParser</code> provides methods to validate
 * and parse component deployment configuration files.
 *
 * <p><strong>Description</strong></p>
 * 
 * The class relies on packages for XML processing to validate the configuration
 * file using the Relax NG schema <code>deployment.rnc</code> assumed to be
 * available in a directory <code>config</code> accessible from the base
 * directory of the running application.  The method
 * <code>parseConfigurationFile</code> parses the file and return the
 * information as an instance of the class <code>ConfigurationParameters</code>
 * that it returns as its result.
 * 
 * TODO: put the schema location in the configuration file?
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true	// TODO
 * </pre>
 * 
 * <p>Created on : 2012-10-26</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ConfigurationFileParser
{
	public static String	SCHEMA_FILENAME = "config" + File.separatorChar +
															"deployment.rnc" ;
	protected DocumentBuilder db ;

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public				ConfigurationFileParser() {
		super();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance() ;
			this.db = dbf.newDocumentBuilder() ;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * validate a configuration file against the configuration Relax NG schema
	 * <code>deployment.rnc</code> assumed to be available in a directory
	 * <code>config</code> accessible from the base directory of the running
	 * application
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param configFile	the File object which reference must be validated.
	 * @return				true if the file is valid, false otherwise
	 */
	public boolean		validateConfigurationFile(File configFile)
	{
		boolean result = false ;
		// Specify you want a factory for RELAX NG
		System.setProperty(
			SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI,
			"com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");
		SchemaFactory factory =
			SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);

		// Load the specific schema you want.
		// Here I load it from a java.io.File, but we could also use a
		// java.net.URL or a javax.xml.transform.Source
		File schemaLocation = new File(SCHEMA_FILENAME);

		// Compile the schema.
		Schema schema = null ;
		try {
			schema = factory.newSchema(schemaLocation);
		} catch (SAXException e) {
			e.printStackTrace() ;
		}
		try {
			// Get a validator from the schema.
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(configFile));
			result = true ;
		} catch (SAXException e1) {
			;
		} catch (IOException e) {
			e.printStackTrace() ;
		}
		return result ;
	}

	/**
	 * parse the configuraiton file and return the information as an instance
	 * of <code>ConfigurationParameters</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param configFile	the File object which reference must be parsed.
	 * @return				the configuration parameters.
	 */
	public ConfigurationParameters	parseConfigurationFile(File configFile) {
		String 		codebaseHostname = null ;
		String		codebaseDirectory = null ;
		String		cyclicBarrierHostname = null ;
		int			cyclicBarrierPort = -1 ;
		String		globalRegistryHostname = null ;
		int			globalRegistryPort = -1 ;
		int			centralRegistryPort = -1 ;
		int 		distributedRegistrySize = -1;
		String 		uname = null ;
		String 		sourcesLocation = null ;
		int			rmiRegistryPort = -1 ;
		String[]	jvms = null ;
		Hashtable<String,String> jvms2hosts = new Hashtable<String,String>() ;
		HashSet<String> rmiRegistryCreators = new HashSet<String>() ;
		HashSet<String> rmiRegistryHosts = new HashSet<String>() ;

		Document doc = null ;
		try {
			doc = this.db.parse(configFile);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		XPath xpathEvaluator = XPathFactory.newInstance().newXPath() ;

		try {
			Node codebaseNode = ((Node)xpathEvaluator.evaluate(
					"/deployment/codebase",
					doc,
					XPathConstants.NODE)) ;
			if (codebaseNode != null) {
				codebaseHostname = ((Node)xpathEvaluator.evaluate(
						"@hostname",
						codebaseNode,
						XPathConstants.NODE)).getNodeValue() ;
				codebaseDirectory = ((Node)xpathEvaluator.evaluate(
						"@directory",
						codebaseNode,
						XPathConstants.NODE)).getNodeValue() ;
			}
			cyclicBarrierHostname = ((Node)xpathEvaluator.evaluate(
					"/deployment/cyclicBarrier/@hostname",
					doc,
					XPathConstants.NODE)).getNodeValue() ;
			cyclicBarrierPort = Integer.parseInt(
					((Node)xpathEvaluator.evaluate(
							"/deployment/cyclicBarrier/@port",
							doc,
							XPathConstants.NODE)).getNodeValue()) ;
			globalRegistryHostname = ((Node)xpathEvaluator.evaluate(
					"/deployment/globalRegistry/@hostname",
					doc,
					XPathConstants.NODE)).getNodeValue() ;
			globalRegistryPort = Integer.parseInt(
					((Node)xpathEvaluator.evaluate(
							"/deployment/globalRegistry/@port",
							doc,
							XPathConstants.NODE)).getNodeValue()) ;
			centralRegistryPort = Integer.parseInt(
					((Node)xpathEvaluator.evaluate(
							"/deployment/globalRegistry/centralRegistry/@port",
							doc,
							XPathConstants.NODE)).getNodeValue()) ;
			distributedRegistrySize = Integer.parseInt(
					((Node)xpathEvaluator.evaluate(
							"/deployment/globalRegistry/centralRegistry/@directorySize",
							doc,
							XPathConstants.NODE)).getNodeValue()) ;
			uname = ((Node)xpathEvaluator.evaluate(
					"/deployment/globalRegistry/centralRegistry/@uname",
					doc,
					XPathConstants.NODE)).getNodeValue() ;
			sourcesLocation = ((Node)xpathEvaluator.evaluate(
					"/deployment/globalRegistry/centralRegistry/@sourcesLocation",
					doc,
					XPathConstants.NODE)).getNodeValue() ;
			rmiRegistryPort = Integer.parseInt(
					((Node)xpathEvaluator.evaluate(
							"/deployment/rmiRegistryPort/@no",
							doc,
							XPathConstants.NODE)).getNodeValue()) ;
			NodeList ns = (NodeList)xpathEvaluator.evaluate(
					"/deployment/jvms2hostnames/jvm2hostname/@jvmuri",
					doc,
					XPathConstants.NODESET) ;
			jvms = new String[ns.getLength()] ;
			for (int i = 0 ; i < ns.getLength() ; i++) {
				jvms[i] = ns.item(i).getNodeValue() ;
			}
			ns = (NodeList)xpathEvaluator.evaluate(
					"/deployment/jvms2hostnames/jvm2hostname",
					doc,
					XPathConstants.NODESET) ;
			for (int i = 0 ; i < ns.getLength() ; i++) {
				String uri =
						((Node)xpathEvaluator.evaluate(
								"@jvmuri", ns.item(i), XPathConstants.NODE)).
															getNodeValue() ;
				String hostname =
						((Node)xpathEvaluator.evaluate(
								"@hostname", ns.item(i), XPathConstants.NODE)).
															getNodeValue() ;
				if (((Node)xpathEvaluator.evaluate(
						"@rmiRegistryCreator", ns.item(i), XPathConstants.NODE)).
						getNodeValue().equals("true")) {
					rmiRegistryCreators.add(uri) ;
					rmiRegistryHosts.add(hostname) ;
				}
				jvms2hosts.put(uri, hostname) ;
			}
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return new ConfigurationParameters(codebaseHostname,
										   codebaseDirectory,
										   cyclicBarrierHostname,
										   cyclicBarrierPort,
										   globalRegistryHostname,
										   globalRegistryPort,
										   centralRegistryPort,
										   distributedRegistrySize,
										   uname,
										   sourcesLocation,
										   rmiRegistryPort,
										   jvms,
										   jvms2hosts,
										   rmiRegistryCreators,
										   rmiRegistryHosts) ;
	}
}
