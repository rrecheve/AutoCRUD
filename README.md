AutoCRUD
===============

Visit the [webpage of the project](http://www.homeria.com/autocrud) to download it as a WebRatio plugin.

What is this project about?
---------------------------

AutoCRUD is basically a CRUD scaffolding tool for [WebRatio](http://http://www.webratio.com/). It increases development productivity by providing WebRatio engineers with a pattern-based development tool for [IFML](http://www.ifml.org/). 
Therefore, engineers basically select a data entity of their project and automatically generate the IFML specification for a CRUD operation by instantiating the right pattern.
Although this work focuses on CRUD patterns, every development team may define new patterns or modified them to build its own repository.

These are currently the main functionalities provided:

* **Parameterized instantiation** of externally defined patterns to the IFML model of a Web application in WebRatio. Users can select a pattern (stored in a file) and parameterize it according to the data model (entities and relations involved). The tool automatically generate the IFML specification according to that pattern instantiation.		
* **Step-by-step IFML generation**. As an alternative to the one-step generation, for teaching purposes, AutoCRUD may produce a step-by-step generation of every IFML element involved in a pattern instantiation.
* **Pattern validation**. Patterns are defined externally to the tool, but when a pattern is loaded a two-step validation process is carried out to assess the pattern specification and its instantiation are valid. 
* **Pattern registry**.	It keeps a registry of all the IFML elements generated for every pattern instantiation. So developers can trace back every IFML element autogenerated. Additionally, a statistical report may be generated to present what patterns are used in every project.

This tool is inspired in the research paper:

 - [AutoCRUD - Automating IFML Specification of CRUD Operations](https://www.researchgate.net/publication/303031780_AutoCRUD_-_Automating_IFML_Specification_of_CRUD_Operations) published at [WebIST'16 conference](http://http://www.webist.org/?y=2016)
 
What can you find in this repository?
-------------------------------------

AutoCRUD has been implemented as an Eclipse plugin project. Therefore, it includes the core implementation of the tool. Additionally, CRUD pattern XML specifications (and pattern XML Schema) are included into the patterns folder. Note that the project actually defines a WebRatio plugin; therefore, the WebRatio Library must be available as a compiler dependency. Alternativelly you may import the project into WebRatio as a Java project, so such dependency is available by default.

Documentation
---
You can find further details about how the tool works in the [Developer Manual](http://www.homeria.com/autocrud/doc) section of the webpage.

Using AutoCRUD 
---

Firstly, you must follow the next installation steps:
1. AutoCRUD is a WebRatio plugin, so you must install [WebRatio Web Platform Community Edition](http://www.webratio.com/site/content/en/pricing#wr-web-platform).
2. Download [AutoCRUD](http://www.homeria.com/autocrud) and move it into WebRatio plugins folder.
3. (Re)start WebRatio.

AutoCRUD provides a set of predefined CRUD patterns, so it can be used off-the-self.

Common workflow:
1. Create a new WebRatio Web project
2. Define your data model
3. Generate a basic Web Model with one SiteView and one Area
4. Launch AutoCRUD
5. Select the target data entity for pattern instantiation
6. Select the pattern to instantiate
7. Select IFML containers in which IFML elements will be generated (SiteView and Area)
8. Parameterized the pattern instantiation according to available data relations and attributes for List and Data units.
9. Press Finish button
10. Either use the one-step generation or the step-by-step one.
11. Continue with WebRatio editors.

You may find more details and examples in the [User Manual](http://www.homeria.com/autocrud/doc).

May I collaborate?
---
Absolutely!. You can follow the typical GitHub contribution flow to ask/contribute:

 - For bugs or feature requests you can directly create an [issue](https://github.com/rrecheve/AutoCRUD/issues). 
 - For implementing new features or directly address any issue, please fork the project, perform the patch and send us a [pull request](https://github.com/rrecheve/AutoCRUD/pulls).

Whatever your contribution is, you will have an answer from us in less than 7 days.

Who is behind this project?
---------------------------
* [Roberto Rodriguez-Echeverria](http://github.com/rrecheve/ "Roberto Rodriguez-Echeverria")
* [Javier Sierra](http://github.com/JavierSierraBlazquez/ "Javier Sierra-Blazquez")
* Juan C. Preciado

License
---
This project is licensed under the [EPL](http://www.eclipse.org/legal/epl-v10.html) license. Documentation is licensed under the under [CC BY 3.0](http://creativecommons.org/licenses/by/3.0/).