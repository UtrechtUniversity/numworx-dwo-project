#!/bin/sh
D=s3://test-dwo-nl/apps
cd target/UploadWidgetGWT
S=UploadWidget.css
aws --profile prod s3 sync --acl public-read --delete $S $D/$S
scp $S $USER@gemini.science.uu.nl:/science/wwwprojects/FI-Sites/www-dev/dwo/apps/
S=UploadWidgetGWT
aws --profile prod s3 sync --acl public-read --delete $S $D/$S

rsync -a --delete $S $USER@gemini.science.uu.nl:/science/wwwprojects/FI-Sites/www-dev/dwo/apps/
