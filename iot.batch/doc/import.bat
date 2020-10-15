set PATH=$PATH;C:\Program Files\MongoDB\Server\4.2\bin\
mongoimport --db testit  --collection=RulesDefinition --file=C:\mq/export/RulesDefinition.json
mongoimport --db testit  --collection=Synonym --file=C:\mq/export/Synonym.json
mongoimport --db testit  --collection=resourceIdentifierImpl --file=C:\mq/export/resourceIdentifierImpl.json
mongoimport --db testit  --collection=userAuthenticationImpl --file=C:\mq/export/userAuthenticationImpl.json
mongoimport --db testit  --collection=Specialday --file=C:\mq\export/Specialday.json