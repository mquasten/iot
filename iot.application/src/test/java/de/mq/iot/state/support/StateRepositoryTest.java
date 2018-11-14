package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.state.State;
import de.mq.iot.state.support.AbstractHomematicXmlApiStateRepository.XmlApiParameters;
import reactor.core.publisher.Mono;

class StateRepositoryTest {

	private static final String PORT = "80";
	private static final String HOST = "kylie.com";
	private static final String PORT_PARAMETER = "port";
	private static final String HOST_PARMETER = "host";
	private final static String XML = "<systemVariables><systemVariable name=\"$name\" variable=\"0\" value=\"$value\" value_list=\"\" ise_id=\"$ise_id\" min=\"\" max=\"\" unit=\"\" type=\"$type\" subtype=\"2\" logged=\"false\" visible=\"true\" timestamp=\"$timestamp\" value_name_0=\"ist falsch\" value_name_1=\"ist wahr\" /></systemVariables>";

	private final static byte[] XML_FUNCTIONS = "<functionList><function name=\"funcButton\" description=\"\" ise_id=\"1221\"><channel address=\"NEQ1415509:1\" ise_id=\"1423\"/><channel address=\"NEQ1415509:2\" ise_id=\"1427\"/><channel address=\"OEQ0281682:1\" ise_id=\"1944\"/><channel address=\"OEQ0281682:2\" ise_id=\"1948\"/><channel address=\"OEQ2305342:1\" ise_id=\"4661\"/><channel address=\"OEQ2305342:2\" ise_id=\"4665\"/></function><function name=\"funcCentral\" description=\"\" ise_id=\"1222\"><channel address=\"BidCoS-RF:1\" ise_id=\"1014\"/><channel address=\"BidCoS-RF:10\" ise_id=\"1050\"/><channel address=\"BidCoS-RF:11\" ise_id=\"1054\"/><channel address=\"BidCoS-RF:12\" ise_id=\"1058\"/><channel address=\"BidCoS-RF:13\" ise_id=\"1062\"/><channel address=\"BidCoS-RF:14\" ise_id=\"1066\"/><channel address=\"BidCoS-RF:15\" ise_id=\"1070\"/><channel address=\"BidCoS-RF:16\" ise_id=\"1074\"/><channel address=\"BidCoS-RF:17\" ise_id=\"1078\"/><channel address=\"BidCoS-RF:18\" ise_id=\"1082\"/><channel address=\"BidCoS-RF:19\" ise_id=\"1086\"/><channel address=\"BidCoS-RF:2\" ise_id=\"1018\"/><channel address=\"BidCoS-RF:20\" ise_id=\"1090\"/><channel address=\"BidCoS-RF:21\" ise_id=\"1094\"/><channel address=\"BidCoS-RF:22\" ise_id=\"1098\"/><channel address=\"BidCoS-RF:23\" ise_id=\"1102\"/><channel address=\"BidCoS-RF:24\" ise_id=\"1106\"/><channel address=\"BidCoS-RF:25\" ise_id=\"1110\"/><channel address=\"BidCoS-RF:26\" ise_id=\"1114\"/><channel address=\"BidCoS-RF:27\" ise_id=\"1118\"/><channel address=\"BidCoS-RF:28\" ise_id=\"1122\"/><channel address=\"BidCoS-RF:29\" ise_id=\"1126\"/><channel address=\"BidCoS-RF:3\" ise_id=\"1022\"/><channel address=\"BidCoS-RF:30\" ise_id=\"1130\"/><channel address=\"BidCoS-RF:31\" ise_id=\"1134\"/><channel address=\"BidCoS-RF:32\" ise_id=\"1138\"/><channel address=\"BidCoS-RF:33\" ise_id=\"1142\"/><channel address=\"BidCoS-RF:34\" ise_id=\"1146\"/><channel address=\"BidCoS-RF:35\" ise_id=\"1150\"/><channel address=\"BidCoS-RF:36\" ise_id=\"1154\"/><channel address=\"BidCoS-RF:37\" ise_id=\"1158\"/><channel address=\"BidCoS-RF:38\" ise_id=\"1162\"/><channel address=\"BidCoS-RF:39\" ise_id=\"1166\"/><channel address=\"BidCoS-RF:4\" ise_id=\"1026\"/><channel address=\"BidCoS-RF:40\" ise_id=\"1170\"/><channel address=\"BidCoS-RF:41\" ise_id=\"1174\"/><channel address=\"BidCoS-RF:42\" ise_id=\"1178\"/><channel address=\"BidCoS-RF:43\" ise_id=\"1182\"/><channel address=\"BidCoS-RF:44\" ise_id=\"1186\"/><channel address=\"BidCoS-RF:45\" ise_id=\"1190\"/><channel address=\"BidCoS-RF:46\" ise_id=\"1194\"/><channel address=\"BidCoS-RF:47\" ise_id=\"1198\"/><channel address=\"BidCoS-RF:48\" ise_id=\"1202\"/><channel address=\"BidCoS-RF:49\" ise_id=\"1206\"/><channel address=\"BidCoS-RF:5\" ise_id=\"1030\"/><channel address=\"BidCoS-RF:50\" ise_id=\"1210\"/><channel address=\"BidCoS-RF:6\" ise_id=\"1034\"/><channel address=\"BidCoS-RF:7\" ise_id=\"1038\"/><channel address=\"BidCoS-RF:8\" ise_id=\"1042\"/><channel address=\"BidCoS-RF:9\" ise_id=\"1046\"/><channel address=\"BidCoS-Wir:1\" ise_id=\"1259\"/><channel address=\"BidCoS-Wir:10\" ise_id=\"1286\"/><channel address=\"BidCoS-Wir:11\" ise_id=\"1289\"/><channel address=\"BidCoS-Wir:12\" ise_id=\"1292\"/><channel address=\"BidCoS-Wir:13\" ise_id=\"1295\"/><channel address=\"BidCoS-Wir:14\" ise_id=\"1298\"/><channel address=\"BidCoS-Wir:15\" ise_id=\"1301\"/><channel address=\"BidCoS-Wir:16\" ise_id=\"1304\"/><channel address=\"BidCoS-Wir:17\" ise_id=\"1307\"/><channel address=\"BidCoS-Wir:18\" ise_id=\"1310\"/><channel address=\"BidCoS-Wir:19\" ise_id=\"1313\"/><channel address=\"BidCoS-Wir:2\" ise_id=\"1262\"/><channel address=\"BidCoS-Wir:20\" ise_id=\"1316\"/><channel address=\"BidCoS-Wir:21\" ise_id=\"1319\"/><channel address=\"BidCoS-Wir:22\" ise_id=\"1322\"/><channel address=\"BidCoS-Wir:23\" ise_id=\"1325\"/><channel address=\"BidCoS-Wir:24\" ise_id=\"1328\"/><channel address=\"BidCoS-Wir:25\" ise_id=\"1331\"/><channel address=\"BidCoS-Wir:26\" ise_id=\"1334\"/><channel address=\"BidCoS-Wir:27\" ise_id=\"1337\"/><channel address=\"BidCoS-Wir:28\" ise_id=\"1340\"/><channel address=\"BidCoS-Wir:29\" ise_id=\"1343\"/><channel address=\"BidCoS-Wir:3\" ise_id=\"1265\"/><channel address=\"BidCoS-Wir:30\" ise_id=\"1346\"/><channel address=\"BidCoS-Wir:31\" ise_id=\"1349\"/><channel address=\"BidCoS-Wir:32\" ise_id=\"1352\"/><channel address=\"BidCoS-Wir:33\" ise_id=\"1355\"/><channel address=\"BidCoS-Wir:34\" ise_id=\"1358\"/><channel address=\"BidCoS-Wir:35\" ise_id=\"1361\"/><channel address=\"BidCoS-Wir:36\" ise_id=\"1364\"/><channel address=\"BidCoS-Wir:37\" ise_id=\"1367\"/><channel address=\"BidCoS-Wir:38\" ise_id=\"1370\"/><channel address=\"BidCoS-Wir:39\" ise_id=\"1373\"/><channel address=\"BidCoS-Wir:4\" ise_id=\"1268\"/><channel address=\"BidCoS-Wir:40\" ise_id=\"1376\"/><channel address=\"BidCoS-Wir:41\" ise_id=\"1379\"/><channel address=\"BidCoS-Wir:42\" ise_id=\"1382\"/><channel address=\"BidCoS-Wir:43\" ise_id=\"1385\"/><channel address=\"BidCoS-Wir:44\" ise_id=\"1388\"/><channel address=\"BidCoS-Wir:45\" ise_id=\"1391\"/><channel address=\"BidCoS-Wir:46\" ise_id=\"1394\"/><channel address=\"BidCoS-Wir:47\" ise_id=\"1397\"/><channel address=\"BidCoS-Wir:48\" ise_id=\"1400\"/><channel address=\"BidCoS-Wir:49\" ise_id=\"1403\"/><channel address=\"BidCoS-Wir:5\" ise_id=\"1271\"/><channel address=\"BidCoS-Wir:50\" ise_id=\"1406\"/><channel address=\"BidCoS-Wir:6\" ise_id=\"1274\"/><channel address=\"BidCoS-Wir:7\" ise_id=\"1277\"/><channel address=\"BidCoS-Wir:8\" ise_id=\"1280\"/><channel address=\"BidCoS-Wir:9\" ise_id=\"1283\"/></function><function name=\"funcClimateControl\" description=\"\" ise_id=\"1216\"/><function name=\"funcEnergy\" description=\"\" ise_id=\"1223\"/><function name=\"funcEnvironment\" description=\"\" ise_id=\"1218\"/><function name=\"funcHeating\" description=\"\" ise_id=\"1215\"/><function name=\"funcLight\" description=\"\" ise_id=\"1214\"/><function name=\"funcSecurity\" description=\"\" ise_id=\"1219\"/><function name=\"funcWeather\" description=\"\" ise_id=\"1217\"/><function name=\"Rolladen\" description=\"Steuerung Rolladenmotoren mit mechanicher oden elektronischer  Endabschaltung \" ise_id=\"1255\"><channel address=\"NEQ1415509:3\" ise_id=\"1431\"/><channel address=\"OEQ0281682:3\" ise_id=\"1952\"/><channel address=\"OEQ2305342:3\" ise_id=\"4669\"/></function><function name=\"Verschluss\" description=\"\" ise_id=\"1220\"/></functionList>".getBytes();

	private final static String XML_ROOMS = "<roomList><room name=\"Arbeitszimmer (oben)\" ise_id=\"1241\"/><room name=\"Badezimmer (unten)\" ise_id=\"1251\"/><room name=\"Diele (oben)\" ise_id=\"1243\"/><room name=\"Eßzimmer (unten)\" ise_id=\"1248\"><channel ise_id=\"4661\"/><channel ise_id=\"4665\"/><channel ise_id=\"4669\"/></room><room name=\"Flur (oben)\" ise_id=\"1242\"/><room name=\"Flur (oben) 1\" ise_id=\"1245\"/><room name=\"Flur (unten)\" ise_id=\"1246\"/><room name=\"Gästezimmer (oben)\" ise_id=\"1244\"/><room name=\"Keller (hinten)\" ise_id=\"1254\"/><room name=\"Keller (vorne)\" ise_id=\"1253\"/><room name=\"Küche (unten)\" ise_id=\"1250\"/><room name=\"Schlafzimmer (oben)\" ise_id=\"1240\"><channel ise_id=\"1423\"/><channel ise_id=\"1427\"/><channel ise_id=\"1431\"/><channel ise_id=\"1944\"/><channel ise_id=\"1948\"/><channel ise_id=\"1952\"/></room><room name=\"Schlafzimmer (unten)\" ise_id=\"1247\"/><room name=\"Terrasse\" ise_id=\"1252\"/><room name=\"Wohnzimmer\" ise_id=\"1249\"/></roomList>";

	private final static byte[] XML_STATES = "<stateList><device name=\"HM-RCV-50 BidCoS-RF\" ise_id=\"1011\"><channel name=\"HM-RCV-50 BidCoS-RF:0\" ise_id=\"1012\" visible=\"\" operate=\"\"><datapoint name=\"BidCos-RF.BidCoS-RF:0.INSTALL_MODE\" type=\"INSTALL_MODE\" ise_id=\"1013\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074628\" operations=\"3\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:1\" ise_id=\"1014\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:1.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1016\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:1.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1017\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:1.LEVEL\" type=\"LEVEL\" ise_id=\"1015\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:2\" ise_id=\"1018\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:2.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1020\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:2.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1021\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:2.LEVEL\" type=\"LEVEL\" ise_id=\"1019\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:3\" ise_id=\"1022\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:3.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1024\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:3.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1025\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:3.LEVEL\" type=\"LEVEL\" ise_id=\"1023\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:4\" ise_id=\"1026\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:4.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1028\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:4.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1029\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:4.LEVEL\" type=\"LEVEL\" ise_id=\"1027\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:5\" ise_id=\"1030\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:5.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1032\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:5.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1033\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:5.LEVEL\" type=\"LEVEL\" ise_id=\"1031\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:6\" ise_id=\"1034\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:6.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1036\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:6.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1037\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:6.LEVEL\" type=\"LEVEL\" ise_id=\"1035\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:7\" ise_id=\"1038\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:7.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1040\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:7.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1041\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:7.LEVEL\" type=\"LEVEL\" ise_id=\"1039\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:8\" ise_id=\"1042\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:8.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1044\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:8.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1045\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:8.LEVEL\" type=\"LEVEL\" ise_id=\"1043\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:9\" ise_id=\"1046\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:9.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1048\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:9.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1049\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:9.LEVEL\" type=\"LEVEL\" ise_id=\"1047\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:10\" ise_id=\"1050\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:10.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1052\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:10.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1053\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:10.LEVEL\" type=\"LEVEL\" ise_id=\"1051\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:11\" ise_id=\"1054\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:11.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1056\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:11.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1057\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:11.LEVEL\" type=\"LEVEL\" ise_id=\"1055\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:12\" ise_id=\"1058\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:12.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1060\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:12.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1061\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:12.LEVEL\" type=\"LEVEL\" ise_id=\"1059\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:13\" ise_id=\"1062\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:13.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1064\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:13.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1065\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:13.LEVEL\" type=\"LEVEL\" ise_id=\"1063\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:14\" ise_id=\"1066\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:14.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1068\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:14.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1069\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:14.LEVEL\" type=\"LEVEL\" ise_id=\"1067\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:15\" ise_id=\"1070\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:15.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1072\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:15.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1073\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:15.LEVEL\" type=\"LEVEL\" ise_id=\"1071\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:16\" ise_id=\"1074\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:16.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1076\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:16.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1077\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:16.LEVEL\" type=\"LEVEL\" ise_id=\"1075\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:17\" ise_id=\"1078\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:17.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1080\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:17.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1081\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:17.LEVEL\" type=\"LEVEL\" ise_id=\"1079\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:18\" ise_id=\"1082\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:18.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1084\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:18.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1085\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:18.LEVEL\" type=\"LEVEL\" ise_id=\"1083\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:19\" ise_id=\"1086\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:19.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1088\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:19.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1089\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:19.LEVEL\" type=\"LEVEL\" ise_id=\"1087\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:20\" ise_id=\"1090\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:20.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1092\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:20.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1093\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:20.LEVEL\" type=\"LEVEL\" ise_id=\"1091\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:21\" ise_id=\"1094\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:21.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1096\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:21.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1097\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:21.LEVEL\" type=\"LEVEL\" ise_id=\"1095\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:22\" ise_id=\"1098\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:22.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1100\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:22.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1101\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:22.LEVEL\" type=\"LEVEL\" ise_id=\"1099\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:23\" ise_id=\"1102\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:23.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1104\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:23.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1105\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:23.LEVEL\" type=\"LEVEL\" ise_id=\"1103\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:24\" ise_id=\"1106\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:24.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1108\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:24.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1109\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:24.LEVEL\" type=\"LEVEL\" ise_id=\"1107\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:25\" ise_id=\"1110\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:25.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1112\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:25.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1113\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:25.LEVEL\" type=\"LEVEL\" ise_id=\"1111\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:26\" ise_id=\"1114\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:26.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1116\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:26.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1117\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:26.LEVEL\" type=\"LEVEL\" ise_id=\"1115\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:27\" ise_id=\"1118\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:27.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1120\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:27.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1121\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:27.LEVEL\" type=\"LEVEL\" ise_id=\"1119\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:28\" ise_id=\"1122\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:28.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1124\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:28.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1125\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:28.LEVEL\" type=\"LEVEL\" ise_id=\"1123\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:29\" ise_id=\"1126\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:29.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1128\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:29.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1129\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:29.LEVEL\" type=\"LEVEL\" ise_id=\"1127\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:30\" ise_id=\"1130\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:30.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1132\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:30.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1133\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:30.LEVEL\" type=\"LEVEL\" ise_id=\"1131\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:31\" ise_id=\"1134\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:31.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1136\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:31.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1137\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:31.LEVEL\" type=\"LEVEL\" ise_id=\"1135\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:32\" ise_id=\"1138\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:32.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1140\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:32.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1141\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:32.LEVEL\" type=\"LEVEL\" ise_id=\"1139\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:33\" ise_id=\"1142\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:33.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1144\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:33.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1145\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:33.LEVEL\" type=\"LEVEL\" ise_id=\"1143\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:34\" ise_id=\"1146\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:34.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1148\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:34.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1149\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:34.LEVEL\" type=\"LEVEL\" ise_id=\"1147\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:35\" ise_id=\"1150\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:35.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1152\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:35.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1153\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:35.LEVEL\" type=\"LEVEL\" ise_id=\"1151\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:36\" ise_id=\"1154\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:36.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1156\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:36.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1157\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:36.LEVEL\" type=\"LEVEL\" ise_id=\"1155\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:37\" ise_id=\"1158\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:37.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1160\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:37.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1161\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:37.LEVEL\" type=\"LEVEL\" ise_id=\"1159\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:38\" ise_id=\"1162\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:38.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1164\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:38.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1165\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:38.LEVEL\" type=\"LEVEL\" ise_id=\"1163\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:39\" ise_id=\"1166\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:39.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1168\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:39.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1169\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:39.LEVEL\" type=\"LEVEL\" ise_id=\"1167\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:40\" ise_id=\"1170\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:40.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1172\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:40.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1173\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:40.LEVEL\" type=\"LEVEL\" ise_id=\"1171\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:41\" ise_id=\"1174\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:41.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1176\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:41.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1177\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:41.LEVEL\" type=\"LEVEL\" ise_id=\"1175\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:42\" ise_id=\"1178\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:42.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1180\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:42.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1181\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:42.LEVEL\" type=\"LEVEL\" ise_id=\"1179\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:43\" ise_id=\"1182\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:43.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1184\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:43.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1185\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:43.LEVEL\" type=\"LEVEL\" ise_id=\"1183\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:44\" ise_id=\"1186\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:44.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1188\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:44.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1189\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:44.LEVEL\" type=\"LEVEL\" ise_id=\"1187\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:45\" ise_id=\"1190\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:45.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1192\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:45.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1193\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:45.LEVEL\" type=\"LEVEL\" ise_id=\"1191\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:46\" ise_id=\"1194\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:46.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1196\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:46.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1197\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:46.LEVEL\" type=\"LEVEL\" ise_id=\"1195\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:47\" ise_id=\"1198\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:47.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1200\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:47.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1201\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:47.LEVEL\" type=\"LEVEL\" ise_id=\"1199\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:48\" ise_id=\"1202\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:48.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1204\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:48.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1205\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:48.LEVEL\" type=\"LEVEL\" ise_id=\"1203\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:49\" ise_id=\"1206\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:49.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1208\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:49.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1209\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:49.LEVEL\" type=\"LEVEL\" ise_id=\"1207\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel><channel name=\"HM-RCV-50 BidCoS-RF:50\" ise_id=\"1210\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-RF.BidCoS-RF:50.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1212\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:50.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1213\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/><datapoint name=\"BidCos-RF.BidCoS-RF:50.LEVEL\" type=\"LEVEL\" ise_id=\"1211\" value=\"\" valuetype=\"6\" valueunit=\"100%\" timestamp=\"0\" operations=\"2\"/></channel></device><device name=\"HMW-LC-Bl1-DR NEQ1415509\" ise_id=\"1409\" unreach=\"false\" sticky_unreach=\"false\" config_pending=\"false\"><channel name=\"HMW-LC-Bl1-DR NEQ1415509:0\" ise_id=\"1410\" visible=\"\" operate=\"\"><datapoint name=\"BidCos-Wired.NEQ1415509:0.UNREACH\" type=\"UNREACH\" ise_id=\"1419\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"5\"/><datapoint name=\"BidCos-Wired.NEQ1415509:0.STICKY_UNREACH\" type=\"STICKY_UNREACH\" ise_id=\"1415\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"7\"/><datapoint name=\"BidCos-Wired.NEQ1415509:0.CONFIG_PENDING\" type=\"CONFIG_PENDING\" ise_id=\"1411\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"5\"/></channel><channel name=\"HMW-LC-Bl1-DR NEQ1415509:1\" ise_id=\"1423\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-Wired.NEQ1415509:1.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1426\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1540657990\" operations=\"6\"/><datapoint name=\"BidCos-Wired.NEQ1415509:1.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1425\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1537186050\" operations=\"6\"/></channel><channel name=\"HMW-LC-Bl1-DR NEQ1415509:2\" ise_id=\"1427\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-Wired.NEQ1415509:2.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1430\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1540657987\" operations=\"6\"/><datapoint name=\"BidCos-Wired.NEQ1415509:2.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1429\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1538746809\" operations=\"6\"/></channel><channel name=\"HMW-LC-Bl1-DR NEQ1415509:3_Fenster links\" ise_id=\"1431\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-Wired.NEQ1415509:3.LEVEL\" type=\"LEVEL\" ise_id=\"1435\" value=\"1.000000\" valuetype=\"4\" valueunit=\"100%\" timestamp=\"1541074629\" operations=\"7\"/><datapoint name=\"BidCos-Wired.NEQ1415509:3.STOP\" type=\"STOP\" ise_id=\"1436\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"2\"/></channel></device><device name=\"HMW-LC-Bl1-DR OEQ0281682\" ise_id=\"1930\" unreach=\"false\" sticky_unreach=\"false\" config_pending=\"false\"><channel name=\"HMW-LC-Bl1-DR OEQ0281682:0\" ise_id=\"1931\" visible=\"\" operate=\"\"><datapoint name=\"BidCos-Wired.OEQ0281682:0.UNREACH\" type=\"UNREACH\" ise_id=\"1940\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"5\"/><datapoint name=\"BidCos-Wired.OEQ0281682:0.STICKY_UNREACH\" type=\"STICKY_UNREACH\" ise_id=\"1936\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"7\"/><datapoint name=\"BidCos-Wired.OEQ0281682:0.CONFIG_PENDING\" type=\"CONFIG_PENDING\" ise_id=\"1932\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"5\"/></channel><channel name=\"HMW-LC-Bl1-DR OEQ0281682:1\" ise_id=\"1944\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-Wired.OEQ0281682:1.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1947\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1540635650\" operations=\"6\"/><datapoint name=\"BidCos-Wired.OEQ0281682:1.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1946\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"6\"/></channel><channel name=\"HMW-LC-Bl1-DR OEQ0281682:2\" ise_id=\"1948\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-Wired.OEQ0281682:2.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"1951\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1540635617\" operations=\"6\"/><datapoint name=\"BidCos-Wired.OEQ0281682:2.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"1950\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1538746808\" operations=\"6\"/></channel><channel name=\"HMW-LC-Bl1-DR OEQ0281682:3_Fenster rechts\" ise_id=\"1952\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-Wired.OEQ0281682:3.LEVEL\" type=\"LEVEL\" ise_id=\"1956\" value=\"1.000000\" valuetype=\"4\" valueunit=\"100%\" timestamp=\"1541074629\" operations=\"7\"/><datapoint name=\"BidCos-Wired.OEQ0281682:3.STOP\" type=\"STOP\" ise_id=\"1957\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"2\"/></channel></device><device name=\"HMW-LC-Bl1-DR OEQ2305342\" ise_id=\"4647\" unreach=\"false\" sticky_unreach=\"false\" config_pending=\"false\"><channel name=\"HMW-LC-Bl1-DR OEQ2305342:0\" ise_id=\"4648\" visible=\"\" operate=\"\"><datapoint name=\"BidCos-Wired.OEQ2305342:0.UNREACH\" type=\"UNREACH\" ise_id=\"4657\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"5\"/><datapoint name=\"BidCos-Wired.OEQ2305342:0.STICKY_UNREACH\" type=\"STICKY_UNREACH\" ise_id=\"4653\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"7\"/><datapoint name=\"BidCos-Wired.OEQ2305342:0.CONFIG_PENDING\" type=\"CONFIG_PENDING\" ise_id=\"4649\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1541074629\" operations=\"5\"/></channel><channel name=\"HMW-LC-Bl1-DR OEQ2305342:1\" ise_id=\"4661\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-Wired.OEQ2305342:1.PRESS_SHORT\" type=\"PRESS_SHORT\" ise_id=\"4664\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1540992724\" operations=\"6\"/><datapoint name=\"BidCos-Wired.OEQ2305342:1.PRESS_LONG\" type=\"PRESS_LONG\" ise_id=\"4663\" value=\"false\" valuetype=\"2\" valueunit=\"\" timestamp=\"1540827713\" operations=\"6\"/></channel><channel name=\"HMW-LC-Bl1-DR OEQ2305342:2\" ise_id=\"4665\" visible=\"true\" operate=\"true\"></channel><channel name=\"HMW-LC-Bl1-DR OEQ2305342:3_Fenster\" ise_id=\"4669\" visible=\"true\" operate=\"true\"><datapoint name=\"BidCos-Wired.OEQ2305342:3.LEVEL\" type=\"LEVEL\" ise_id=\"4673\" value=\"1.000000\" valuetype=\"4\" valueunit=\"100%\" timestamp=\"1541074629\" operations=\"7\"/><datapoint name=\"BidCos-Wired.OEQ2305342:3.STOP\" type=\"STOP\" ise_id=\"4674\" value=\"\" valuetype=\"2\" valueunit=\"\" timestamp=\"0\" operations=\"2\"/></channel></device></stateList>".getBytes();

	private final static byte[] XML_VERSION = "<version>1.15</version>".getBytes();
	
	private static final String ID = "4711";

	private static final String TIMESTAMP = "" + new Date().getTime() / 1000;

	private static final String BOOLEAN_TYPE = "2";

	private static final String WORKINGDAY = "Workingday";

	private static final String URI = "uri";

	private AbstractHomematicXmlApiStateRepository stateRepository = Mockito.mock(AbstractHomematicXmlApiStateRepository.class, Mockito.CALLS_REAL_METHODS);

	private final WebClient.Builder webClientBuilder = Mockito.mock(WebClient.Builder.class);

	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);

	@SuppressWarnings("unchecked")
	private final ResponseEntity<byte[]> resonseEntity = Mockito.mock(ResponseEntity.class);

	private final Duration duration = Duration.ofMillis(500);

	private ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
	@SuppressWarnings("unchecked")
	private ArgumentCaptor<Map<String, String>> parameterCaptor = ArgumentCaptor.forClass(Map.class);
	final Map<Class<?>, Object> dependencies = new HashMap<>();

	@BeforeEach
	void setup() throws IOException {

		dependencies.put(ConversionService.class, new DefaultConversionService());

		dependencies.put(Duration.class, duration);
		Arrays.asList(AbstractHomematicXmlApiStateRepository.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(stateRepository, field.getName(), dependencies.get(field.getType())));

		final Map<String, String> parameter = new HashMap<>();
		parameter.put(HOST_PARMETER, HOST);
		parameter.put(PORT_PARAMETER, PORT);

		Mockito.doReturn(parameter).when(resourceIdentifier).parameters();

		final XPath xpath = XPathFactory.newInstance().newXPath();

		Mockito.doReturn(xpath).when(stateRepository).xpath();

		Mockito.doReturn(URI).when(resourceIdentifier).uri();

		Mockito.doReturn(webClientBuilder).when(stateRepository).webClientBuilder();
		final WebClient webClient = Mockito.mock(WebClient.class);
		Mockito.doReturn(webClient).when(webClientBuilder).build();
		final RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
		RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RequestBodyUriSpec.class);
		Mockito.doReturn(requestBodyUriSpec).when(webClient).put();
		Mockito.doReturn(requestHeadersUriSpec).when(webClient).get();

		final RequestBodySpec requestBodySpec = Mockito.mock(RequestBodySpec.class);
		final RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
		Mockito.doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(uriCaptor.capture(), parameterCaptor.capture());

		Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri(uriCaptor.capture(), parameterCaptor.capture());
		@SuppressWarnings("unchecked")
		final Mono<ClientResponse> mono = Mockito.mock(Mono.class);
		Mockito.doReturn(mono).when(requestHeadersSpec).exchange();

		Mockito.doReturn(mono).when(requestBodySpec).exchange();
		final ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
		Mockito.doReturn(clientResponse).when(mono).block(duration);
		@SuppressWarnings("unchecked")
		final Mono<ResponseEntity<String>> monoResponseEntity = Mockito.mock(Mono.class);
		Mockito.doReturn(monoResponseEntity).when(clientResponse).toEntity(byte[].class);

		Mockito.doReturn(monoResponseEntity).when(clientResponse).toEntity(String.class);

		Mockito.doReturn(resonseEntity).when(monoResponseEntity).block(duration);
		Mockito.doReturn(HttpStatus.OK).when(resonseEntity).getStatusCode();

	}

	@Test
	final void findStates() {

		final String xml = XML.replaceFirst("\\$" + StateConverter.KEY_NAME, WORKINGDAY).replaceFirst("\\$" + StateConverter.KEY_VALUE, "" + true).replaceFirst("\\$" + StateConverter.KEY_TYPE, BOOLEAN_TYPE).replaceFirst("\\$" + StateConverter.KEY_TIMESTAMP, TIMESTAMP)
				.replaceFirst("\\$" + StateConverter.KEY_ID, ID);

		Mockito.when(resonseEntity.getBody()).thenReturn(xml.getBytes());

		final Collection<Map<String, String>> results = stateRepository.findStates(resourceIdentifier);

		assertEquals(1, results.size());
		final Map<String, String> result = results.stream().findAny().get();
		assertEquals(ID, result.get(StateConverter.KEY_ID));
		assertEquals(WORKINGDAY, result.get(StateConverter.KEY_NAME));
		assertEquals(BOOLEAN_TYPE, result.get(StateConverter.KEY_TYPE));
		assertEquals(Boolean.TRUE.toString(), result.get(StateConverter.KEY_VALUE));
		assertEquals(TIMESTAMP, result.get(StateConverter.KEY_TIMESTAMP));

		assertEquals(URI, uriCaptor.getValue());
		assertEquals(HOST, parameterCaptor.getValue().get(HOST_PARMETER));
		assertEquals(PORT, parameterCaptor.getValue().get(PORT_PARAMETER));
		assertEquals(XmlApiParameters.Sysvarlist.resource(), parameterCaptor.getValue().get(XmlApiParameters.RESOURCE_PARAMETER_NAME));

	}

	@Test
	final void findStatesInternalServerError() {
		Mockito.doReturn(HttpStatus.INTERNAL_SERVER_ERROR).when(resonseEntity).getStatusCode();

		assertThrows(HttpStatusCodeException.class, () -> stateRepository.findStates(resourceIdentifier));
	}

	@Test
	final void findStatesBadXml() {
		Mockito.doReturn("<xml>".getBytes()).when(resonseEntity).getBody();

		assertThrows(IllegalStateException.class, () -> stateRepository.findStates(resourceIdentifier));
	}

	@Test
	final void create() throws NoSuchMethodException, SecurityException {

		final Constructor<?> con = stateRepository.getClass().getDeclaredConstructor(ConversionService.class, Long.class);
		final Object stateRepository = BeanUtils.instantiateClass(con, dependencies.get(ConversionService.class), Long.valueOf(((Duration) dependencies.get(Duration.class)).toMillis()));

		final Map<Class<?>, ?> dependencyMap = Arrays.asList(AbstractHomematicXmlApiStateRepository.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType()))
				.collect(Collectors.toMap(field -> field.getType(), field -> ReflectionTestUtils.getField(stateRepository, field.getName())));
		assertEquals(2, dependencyMap.size());
		assertEquals(dependencies, dependencyMap);
	}

	@Test
	final void changeState() {

		final String xml = "<result><changed/></result>";
		Mockito.doReturn(xml).when(resonseEntity).getBody();

		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();

		stateRepository.changeState(resourceIdentifier, state);

		assertEquals(URI + AbstractHomematicXmlApiStateRepository.STATE_CHANGE_URL_PARAMETER, uriCaptor.getValue());
		assertEquals(HOST, parameterCaptor.getValue().get(HOST_PARMETER));
		assertEquals(PORT, parameterCaptor.getValue().get(PORT_PARAMETER));
		assertEquals(XmlApiParameters.ChangeSysvar.resource(), parameterCaptor.getValue().get(XmlApiParameters.RESOURCE_PARAMETER_NAME));
	}

	@Test
	final void changeStateNotFound() {
		final String xml = "<result><not_found/></result>";
		Mockito.doReturn(xml).when(resonseEntity).getBody();

		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();

		assertHttpExceptionIsThrown(state, HttpStatus.NOT_FOUND);

	}

	private void assertHttpExceptionIsThrown(final State<?> state, HttpStatus expectedHttpStatusCode) {
		try {

			stateRepository.changeState(resourceIdentifier, state);
			fail(HttpStatusCodeException.class.getName() + " should be thrown.");
		} catch (final HttpStatusCodeException e) {
			assertEquals(expectedHttpStatusCode, e.getStatusCode());
		}
	}

	@Test
	final void changeStateUnkownResult() {
		final String xml = "<result><unkown/></result>";
		Mockito.doReturn(xml).when(resonseEntity).getBody();

		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();

		assertHttpExceptionIsThrown(state, HttpStatus.BAD_REQUEST);

	}

	@Test
	final void changeStateMissingResult() {
		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();
		assertHttpExceptionIsThrown(state, HttpStatus.BAD_REQUEST);
	}

	@Test
	final void changeStateEmptyResult() {
		final String xml = "<result/>";
		Mockito.doReturn(xml).when(resonseEntity).getBody();

		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();
		assertHttpExceptionIsThrown(state, HttpStatus.BAD_REQUEST);
	}

	@Test
	final void findChannelIds() {
		Mockito.doReturn(XML_FUNCTIONS).when(resonseEntity).getBody();

		final Collection<Long> results = ((AbstractHomematicXmlApiStateRepository) stateRepository).findChannelIds(resourceIdentifier, "Rolladen");

		assertEquals(Arrays.asList(1431L, 1952L, 4669L), results);

		assertEquals(URI, uriCaptor.getValue());
		assertEquals(HOST, parameterCaptor.getValue().get(HOST_PARMETER));
		assertEquals(PORT, parameterCaptor.getValue().get(PORT_PARAMETER));
		assertEquals(XmlApiParameters.FunctionList.resource(), parameterCaptor.getValue().get(XmlApiParameters.RESOURCE_PARAMETER_NAME));

	}

	@Test
	final void findCannelsRooms() throws UnsupportedEncodingException {
		Mockito.doReturn(XML_ROOMS.getBytes("ISO-8859-1")).when(resonseEntity).getBody();

		final Map<Long, String> results = ((AbstractHomematicXmlApiStateRepository) stateRepository).findCannelsRooms(resourceIdentifier);

		assertEquals(9, results.size());
		Arrays.asList(4661L, 4665L, 4669L).forEach(id -> assertEquals("Eßzimmer (unten)", results.get(id)));

		Arrays.asList(1423L, 1427L, 1431L, 1944L, 1948L, 1952L).forEach(id -> assertEquals("Schlafzimmer (oben)", results.get(id)));

	}

	@Test
	final void findDeviceStates() {
		Mockito.doReturn(XML_STATES).when(resonseEntity).getBody();
		final Collection<State<Double>> results = ((AbstractHomematicXmlApiStateRepository) stateRepository).findDeviceStates(resourceIdentifier);

		assertEquals(3, results.size());
		results.stream().map(result -> result.value()).forEach(value -> assertTrue(value >= 0d && value <= 1d));
		assertEquals(Arrays.asList(1431L, 1952L, 4669L), results.stream().map(result -> new Long(result.id())).collect(Collectors.toList()));
		results.stream().map(result -> result.name()).forEach(name -> assertTrue(name.matches(".*:3.Fenster.*")));
	}
	
	@Test
	final void findVersion() {
		Mockito.doReturn(XML_VERSION).when(resonseEntity).getBody();
		final double version = stateRepository.findVersion(resourceIdentifier);
		assertEquals(1.15, version);
	}

}