# source-compare-maven-plugin
Compares the HTML source between local and deployed HTML.

```xml
<plugin>
    <groupId>me.danchapman.maven.plugins</groupId>
    <artifactId>source-compare-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
      <configuration>
        <testsFilePath>src/main/resources/tests.xml</testsFilePath>
        <siteURL>http://localhost:4503</siteURL>
        <compareSourceFolderPath>src/main/resources/tests</compareSourceFolderPath>
      </configuration>
        <executions>
        <execution>
            <phase>verify</phase>
            <goals>
                <goal>compare</goal>
                </goals>
        </execution>
    </executions>
</plugin>
```

tests.xml

```xml
<Tests>
	<test compareSourceFilePath="content-tile.html" urlPath="/content/sample.html" cssSelector="div.content-tile" />
</Tests>
```

content-tile.html

```html
<div class="content-tile section">
 <div class="content-tile-container">
  <p class="content-tile-title"> Test </p>
  <figure>
   <picture>
    <!--[if IE 9] ><video style="display: none;"><![endif]-->
    <source srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg/_jcr_content/renditions/cq5dam.web.1600.9600.jpeg" media="(min-width: 992px)">
    <source srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg/_jcr_content/renditions/cq5dam.web.780.4680.jpeg" media="(max-width: 991px)">
    <!--[if IE 9]></video><![endif]-->
    <img srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg">
   </picture>
   <figcaption>
    <h2>Sample Title</h2>
    <p class="content-tile-subtitle">Sample Subtitle</p>
    <p>Some sample text.</p>
   </figcaption>
  </figure>
 </div>
</div>
```

Output:

```text
[INFO] PASS! Compared content-tile.html to /content/sample.html
```

```text
[ERROR] FAIL! Compared content-tile.html to /content/sample.html
[ERROR] COMPARE SOURCE: <div class="content-tile section"> 
 <div class="content-tile-container"> 
  <p class="content-tile-title"> Test </p> 
  <figure> 
   <picture> 
    <!--[if IE 9] ><video style="display: none;"><![endif]--> 
    <source srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg/_jcr_content/renditions/cq5dam.web.1600.9600.jpeg" media="(min-width: 992px)"> 
    <source srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg/_jcr_content/renditions/cq5dam.web.780.4680.jpeg" media="(max-width: 991px)"> 
    <!--[if IE 9]></video><![endif]--> 
    <img srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg"> 
   </picture> 
   <div> 
    <h2>Sample Title</h2> 
    <p class="content-tile-subtitle">Sample Subtitle</p> 
    <p>Some sample text.</p> 
   </div> 
  </figure> 
 </div> 
</div>
[ERROR] SOURCE: <div class="content-tile section"> 
 <div class="content-tile-container"> 
  <p class="content-tile-title"> Test </p> 
  <figure> 
   <picture> 
    <!--[if IE 9] ><video style="display: none;"><![endif]--> 
    <source srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg/_jcr_content/renditions/cq5dam.web.1600.9600.jpeg" media="(min-width: 992px)"> 
    <source srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg/_jcr_content/renditions/cq5dam.web.780.4680.jpeg" media="(max-width: 991px)"> 
    <!--[if IE 9]></video><![endif]--> 
    <img srcset="/content/dam/Lenovo-p780-camera-sample-10.jpg"> 
   </picture> 
   <figcaption> 
    <h2>Sample Title</h2> 
    <p class="content-tile-subtitle">Sample Subtitle</p> 
    <p>Some sample text.</p> 
   </figcaption> 
  </figure> 
 </div> 
</div>
[ERROR] DIFFERENCE: figcaption> 
    <h2>Sample Title</h2> 
    <p class="content-tile-subtitle">Sample Subtitle</p> 
    <p>Some sample text.</p> 
   </figcaption> 
  </figure> 
 </div> 
</div>
```