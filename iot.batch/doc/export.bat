
set PATH=$PATH;C:\Program Files\MongoDB\Server\4.2\bin\
mongoexport --db iot  --collection=RulesDefinition --out=C:\mq/export/RulesDefinition.json
mongoexport --db iot  --collection=Synonym --out=C:\mq/export/Synonym.json
mongoexport --db iot  --collection=resourceIdentifierImpl --out=C:\mq/export/resourceIdentifierImpl.json
mongoexport --db iot  --collection=userAuthenticationImpl --out=C:\mq/export/userAuthenticationImpl.json
REM mongoexport --db iot2  --collection=Specialday --out=C:\mq\export/Specialday.json