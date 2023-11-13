#!/bin/sh
D=s3://test-dwo-nl/apps
cd target/UploadWidgetGWT
S=UploadWidgetGWT.css
aws --profile prod s3 cp --acl public-read  $S $D/
scp $S $USER@gemini.science.uu.nl:/science/wwwprojects/FI-Sites/www-dev/dwo/apps/
S=UploadWidgetGWT
aws --profile prod s3 cp --acl public-read --recursive $S $D/$S

rsync -a --delete $S $USER@gemini.science.uu.nl:/science/wwwprojects/FI-Sites/www-dev/dwo/apps/
