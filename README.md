# numworx-dwo-project

The Numworx DWO project contains all source code that builds a Java server WAR archive. 
Together with a second github project numworx-dwo-resources that contains the content delivery service, 
it creates the Learning Management platform "Numworx". 
  
## Introduction

This project builds the main platform service. To build you need a Github login and token. 

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

Each folder contains a maven module, the most important are:
 * DWOplayer, this is the player that executes all activities and navigates between modules.
 * DWOGWTCliemt, a shell around the player, that fullfills supporting tasks.
 * DWOServer, the REST server, delivering data for activities and the user specific storage
 * DWOJClient, a Java application for authoring and previewing activities and supporting tasks, like user management. Overlap with DWOGWTClient the html5 application
 
## Usage

The main artifact of this project is the EBServer war which can run in a Servlet/JSP container like Tomcat 9.0.
Several external services are needed.
* a MySQL database server, version 8.0 or compatible. Postgres is also possible.
* a redis compatible server. Used for session caching. Maven profile `-Predis-jcache`
* a xmpp chat server, like prosody, configured with the EBServer as the oidc provider.
* a JupiterHub server to run notebooks inside the web application, e.g. the TLJH, also with EBServer as oidc provider
* a content delivery server, like the one that is build in the numworx-dwo-resources project.
* a SMTP server to send 'password forgotten' mails to.
* an AWS S3, or compatible, or an Azure blob store. See maven profile `-Pupload`

### Configuration
The EBServer war can be configured with lots of properties:
* `ALLOW_ORIGIN` the url of the Servlet/JSP container, like https://app.dwo.nl
* `AZ_SHAREDKEY` Key to access the Azure blob store, the s3_bucket name is the account name
* `CDN_HOST` Host:port/prefix of the resource server
* `CDNURL` CDN_HOST plus scheme like https://
* `DWO_ELB` IPv4 prefix for a Loadbalancer. e.g. 172.31.
* `DWO_ENV` app or test? And extra features like entree, kiosk.
* `DWO_REDIRECT` should http redirect to https? Some loadbalancers do that already.
* `ENV_IPRANGE` IPv4 or IPv6 ranges to restrict exams, default 0.0.0.0/0, ::/0
* `ENV_NOSEB` if true, don't use sebs:// urls to start the Save Exam Browser.
* `ENV_ORGID` The default school, when users log in via OIDC.
* `PROFILE_EXTENSION` Choose variant profile selector in the DWOJClient
* mysql://`RDS_HOSTNAME`:`RDS_PORT`/`RDS_DB_NAME`?username=`RDS_USERNAME`&password=`RDS_PASSWORD` parts of the jdbc url for mysql
* `REDIS` hostname of the redis server
* `REDIS_PREFIX` select between redis or rediss (redis over ssl)
* `S3_BUCKET` name of the AWS S3 bucket where uploaded files are stored.
* `SEB_TOETS_MAC`,`SEB_TOETS_WIN` allowed hashes for Safe Exam Browser. Takes some experimentation.
* `SMTP_AUTH`,`SMTP_EMAIL`,`SMTP_PASSWORD`,`SMTP_PORT`,`SMTP_SERVER`,`SMTP_SSL`,`SMTP_TLS`,`SMTP_USER` configuration of the JavaMail library. If the k8s configuration is used, you may configure your smtp container instead.
 * `UU_INDEX` alternative home page
 * `UU_DOWNLOAD` alternative downloads page

## License

This work is licensed under the GNU General Public License version 3.
Copyright © 2026, Utrecht University, all rights reserved.

## Contact 
[Wim van Velthoven](mailto:w.p.g.vanvelthoven@uu.nl)
