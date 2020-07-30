

cp -- /tmp/src/ticket-system-service/conf/ocp/logback.xml "$CONF_DIR/logback.xml"
cp -- /tmp/src/ticket-system-service/conf/ticket-system-service.yaml "$CONF_DIR/ticket-system-service.yaml"

sed -i 's/localhost/memcached/' "$CONF_DIR/ticket-system-service.yaml"
 
ln -s -- "$TOMCAT_APPS/ticket-system-service.xml" "$DEPLOYMENT_DESC_DIR/ticket-system-service.xml"
