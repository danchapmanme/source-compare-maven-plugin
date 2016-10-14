/**
 * Created by danchap on 11/10/2016.
 */

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Mojo(name = "compare")
public class CompareMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * The tests file path.
     */
    @Parameter(property = "run.testsFilePath")
    private String testsFilePath;

    /**
     * The site URL.
     */
    @Parameter(property = "run.siteURL")
    private String siteURL;

    /**
     * The compare source folder path
     */
    @Parameter(property = "run.compareSourceFolderPath")
    private String compareSourceFolderPath;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Running source tests...");

        File testsFile = getFile(testsFilePath);

        if (!testsFile.exists()) {
            throw new MojoFailureException("Tests file not found! " + testsFile.getAbsolutePath());
        } else if (testsFile.isDirectory()) {
            throw new MojoFailureException("Tests file is a directory!");
        }

        File compareSourceFolder = getFile(compareSourceFolderPath);

        if (compareSourceFolderPath != null && !compareSourceFolder.isDirectory()) {
            throw new MojoFailureException("Compare source folder not found!");
        }

        boolean failed = false;

        try {

            CloseableHttpClient httpClient = HttpClients.createDefault();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(testsFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("test");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String compareSourceFilePath = eElement.getAttribute("compareSourceFilePath");
                    String urlPath = eElement.getAttribute("urlPath");
                    String cssSelector = eElement.getAttribute("cssSelector");

                    HttpGet httpGet = new HttpGet(siteURL + urlPath);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    int statusCode = httpResponse.getStatusLine().getStatusCode();

                    String source = null;
                    if (statusCode == HttpStatus.SC_OK) {
                        InputStream is = httpResponse.getEntity().getContent();
                        source = IOUtils.toString(is, "UTF-8");
                    }

                    if (StringUtils.isNotEmpty(cssSelector)) {
                        org.jsoup.nodes.Document htmlDoc = Jsoup.parse(source, "UTF-8");
                        org.jsoup.nodes.Element partialHtml = htmlDoc.select(cssSelector).first();
                        source = partialHtml.outerHtml();
                    }

                    File compareSourceFile = getFile(compareSourceFolder.getPath() + "/" + compareSourceFilePath);
                    String compareSource = FileUtils.readFileToString(compareSourceFile, "UTF-8");

                    String difference = StringUtils.difference(compareSource, StringUtils.defaultString(source));

                    if (StringUtils.isNotEmpty(difference)) {
                        getLog().error(String.format("FAIL! Compared %s to %s", compareSourceFilePath, urlPath));
                        getLog().error("COMPARE SOURCE: " + compareSource);
                        getLog().error("SOURCE: " + source);
                        getLog().error("DIFFERENCE: " + difference);
                        failed = true;
                    } else {
                        getLog().info(String.format("PASS! Compared %s to %s", compareSourceFilePath, urlPath));
                    }

                }

            }

        } catch (SAXException e) {
            getLog().error("Error parsing tests file.");
            failed = true;
        } catch (ClientProtocolException e) {
            getLog().error("Error getting URL response.");
            failed = true;
        } catch (ParserConfigurationException e) {
            getLog().error("");
            failed = true;
        } catch (IOException e) {
            getLog().error("General IO error.");
            failed = true;
        }

        if (failed) {
            throw new MojoFailureException("Build Failed.");
        }

    }

    private File getFile(String path)
    {
        File file = new File( path );
        if ( !file.isAbsolute() )
        {
            return new File( project.getBasedir(), path );
        }
        return file;
    }

}
