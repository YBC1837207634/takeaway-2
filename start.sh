
echo ==================start check ==============
p_name="take"
pid=`ps -ef | grep ${p_name} | grep -v grep | awk '{print $2}'` 
if [ ${pid} ]; then
     echo "shutting down~~~~~~"
     kill -15 $pid
     sleep 3s   # wait 3s
     echo "closed~~~"
else 
     echo "closed"
fi

echo pull~~~~
git pull
echo pull over!

mvn clean package -Dmaven.test.skip=true
cd target
echo start object!
app_name=`ls | grep jar | grep -v ori`
echo $app_name 
nohup java -jar $app_name &> log_2.log &
echo OK!




