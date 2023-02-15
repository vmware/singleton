# How to use custom log configuration file in Singleton service

## 1.The custom log configuration sample
If you want to use custom log configuration file, you can change the configuration sample as following:
```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="OFF">
<!-- Logging Properties -->
	<Properties>
		<property name="application_name">Singleton</property>
		<property name="module_name">i18nManager</property>
		<property name="log_path">${sys:app.log.home:-./logs}/i18n</property>
	</Properties>
	<Appenders>
       <!-- Console Appender configuration-->
		<Console name="Console" target="SYSTEM_OUT">
			<ThresholdFilter level="trace" onMatch="ACCEPT"
							 onMismatch="DENY"/>
			<PatternLayout
					pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [${application_name}] [${module_name}] [%level] [%t]  %l - %msg%n"/>
		</Console>
        <!-- Info level log File Appenders configuration -->
		<RollingFile name="InfoFile"
					 fileName="${log_path}/${application_name}-info.log"
					 filePattern="${log_path}/${application_name}-info-%d{yyyy-MM-dd}.%i.log">
			<ThresholdFilter level="info" onMatch="ACCEPT"
							 onMismatch="DENY"/>
			<PatternLayout
					pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [${application_name}] [${module_name}] [%level] [%t]  %l - %msg%n"/>
			<Policies>
				<TimeBasedTriggeringPolicy/>
				<SizeBasedTriggeringPolicy size="64 MB"/>
			</Policies>
			<DefaultRolloverStrategy fileIndex="nomax">
				<Delete basePath="${log_path}" maxDepth="1">
					<IfFileName glob="${application_name}-info-*.log"/>
                     <!-- Delete All older files except the last created 10 log files -->
					<IfAccumulatedFileCount exceeds="10"/>
                  <!-- 
                    <!-- Delete all files older than 7 days -->
                    <IfLastModified age="7d"/>
                    <!-- As long as the total log file size exceeds 1GB, the oldest log file in chronological order will be deleted -->
					<IfAccumulatedFileSize exceeds="3GB"/>
                 -->
				</Delete>
			</DefaultRolloverStrategy>
		</RollingFile>
      <!-- Error level log File Appenders configuration -->
		<RollingFile name="ErrorFile"
					 fileName="${log_path}/${application_name}-error.log"
					 filePattern="${log_path}/${application_name}-error-%d{yyyy-MM-dd}.%i.log">
			<ThresholdFilter level="error" onMatch="ACCEPT"
							 onMismatch="DENY"/>
			<PatternLayout
					pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [${application_name}] [${module_name}] [%level] [%t]  %l - %msg%n"/>
			<Policies>
				<TimeBasedTriggeringPolicy/>
				<SizeBasedTriggeringPolicy size="64 MB"/>
			</Policies>
			<DefaultRolloverStrategy fileIndex="nomax">
				<Delete basePath="${log_path}" maxDepth="1">
					<IfFileName glob="${application_name}-error-*.log"/>
                  <!-- Delete All older files except the last created 5 log files -->
					<IfAccumulatedFileCount exceeds="5"/>              
                 <!-- 
                    <!-- Delete all files older than 7 days -->
                    <IfLastModified age="7d"/
                    <!-- As long as the total log file size exceeds 1GB, the oldest log file in chronological order will be deleted -->
					<IfAccumulatedFileSize exceeds="1GB"/>
                 -->
				</Delete>
			</DefaultRolloverStrategy>
		</RollingFile>
	</Appenders>
	<Loggers>
		<Root level="debug">
			<appender-ref ref="Console"/>
			<appender-ref ref="InfoFile"/>
			<appender-ref ref="ErrorFile"/>
		</Root>
	</Loggers>
</Configuration>
```

According your requirement, you can change the following item as following：

+ *`<SizeBasedTriggeringPolicy size="64 MB"/>`* You can change the single log size
+ *`<IfAccumulatedFileCount exceeds="5"/>`*  You can change delete strategy how many keep the last created logs file
+ *<`IfAccumulatedFileSize exceeds="1GB"/>`* You can define which total log files exceeds size, the oldest log file in chronological order will be deleted 

## How to start the singleton service with custom log configuration file
 You can use the following command start the singleton service:

`java -jar singleton-xxx.jar --logging.config=file:./log4j2-spring.xml`

The file:./log4j2-spring.xml is your log configuration location. You can use relative address or absolute address.
