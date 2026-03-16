# numworx-dwo-project

The Numworx DWO project contains all source code that builds a Java server WAR archive. 
Together with a second github project numworx-dwo-resources that contains the content delivery service, 
it creates the Learning Management system "Numworx". 
  
## Introduction

In this section, provide an overview of your code and describe the
project in which the code was developed. Highlight the purpose,
scope, and potential uses of your code. Also, consider including
links to relevant publications or resources that provide additional
context.

## Prerequisites

Apart from many libraries from the maven java central repository, the following github projects deliver imported artifacts
 * numworx-vendor-geogebra
 * numworx-vendor-cbook
 * numworx-vendor-jxbrowser
 * numworx-vendor-oauth
 * numworx-dwo-openmath
 * numworx-dwo-gwtclienthtmlui
 
## Contents 

### Folder structure

Describe the organization of your package, including the contents of
each folder and the files it contains. Use tables or file trees to
make it easy for users to understand your folder structure. Describe
where results and figures are stored if not added to the project
folder.

### File formats 

Describe the file format(s) used in your project and the software
required to open them. 

## Usage

The main artifact of this project is the EBServer war which can run in a Servlet/JSP container like Tomcat 9.0.
Several external services are needed.
* a MySQL database server, version 8.0 or compatible. Postgres is also possible.
* a redis compatible server. Used for session caching. Maven profile `-Predis-jcache`
* a xmpp chat server, like prosody, configured with the EBServer as the oidc provider.
* a JupiterHub server to run notebooks inside the web application, e.g. the TLJH, also with EBServer as oidc provider
* a content delivery server, like the one that is build in the numworx-dwo-resources project.
* a SMTP server to send 'password forgotten' mails to.
* an AWS S3, or compatible, or an Azure file store. Maven profile `-Pupload`

### Configuration
The EBServer war can be configured with lots of properties:
* `ALLOW_ORIGIN` the url of the Servlet/JSP container, like https://app.dwo.nl
* `DWO_ELB` IPv4 prefix for a Loadbalancer. e.g. 172.31.
* `DWO_ENV` app or test?
* `DWO_REDIRECT` should http redirect to https? Some loadbalancers do that already.

## License

This work is licensed under the GNU General Public License version 3.
Copyright © 2026, Utrecht University, all rights reserved.

## Contact 
[Wim van Velthoven](mailto:w.p.g.vanvelthoven@uu.nl)
