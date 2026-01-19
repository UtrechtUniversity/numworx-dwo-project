#!/bin/sh

cd target/uploadwidgetgwt
S=UploadWidgetGWT.css
cp $S $HOME/Public/apps/
S=UploadWidgetGWT
rm -rf $HOME/Public/apps/$S
cp -r  $S $HOME/Public/apps/
