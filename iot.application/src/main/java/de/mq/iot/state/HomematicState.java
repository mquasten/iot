package de.mq.iot.state;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="systemVariable")
@XmlAccessorType(XmlAccessType.FIELD)
public class HomematicState {
	@XmlAttribute(name="ise_id")
	private  Long id ;

	@XmlAttribute
	private String name; 
	
	@XmlAttribute(name="min")
	private Integer min;

	public Integer getMin() {
		return min;
	}

	
	public String getName() {
		return name;
	}

	
	public Long getId() {
		return id;
	}

	

	

}
