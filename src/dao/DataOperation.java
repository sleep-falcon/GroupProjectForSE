package dao;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEW;
import dao.allDo.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author yu
 */
public class DataOperation {
    /**
     * add a member into the xml to store
     *
     * @param user a DO of a user in the gym
     * @return true when success, false when fail
     */
    public static boolean addUser(UserDO user) {


        try {

            SAXReader reader = new SAXReader();

            File xmlFile = null;
            int type; //use to record the type of the user

            if(user.getClass() == MemberDO.class) {
                type = 0;
            }
            else if(user.getClass() == TrainerDO.class){
                type = 1;
            }
            else if(user.getClass() == AdminDO.class){
                type = 2;
            }
            else if(user.getClass() == PromoterDO.class){
                type = 3;
            }
            else{
                type = -1;
                System.out.println("Not a correct type of user!");
                return false;
            }

            xmlFile = new File(UserTypeEnum.getPos(type));

            Document doc = reader.read(xmlFile);

            Element root = doc.getRootElement();

            Element newElement = root.addElement(UserTypeEnum.getName(type));

            newElement.addAttribute("id", user.getId());
            newElement.addAttribute("password", user.getPassword());
            newElement.addAttribute("name", user.getName());
            newElement.addAttribute("phoneNumber", user.getPhoneNumber());
            newElement.addAttribute("info", user.getInfo());

            if(type == 0) {
                //if we want to add a member
                MemberDO member = (MemberDO) user;
                newElement.addAttribute("type", member.getType());
                newElement.addAttribute("email", member.getEmail());
            }
            else if(type == 1){
                //if we want to add a trainer
            }
            else if(type == 2){
                //if we want to add a admin
            }
            else if(type == 3){
                //if we want to add a promoter
            }

            Writer out = new PrintWriter(UserTypeEnum.getPos(type), "UTF-8");

            //format control
            // OutputFormat format = new OutputFormat("\t", true);
            //format.setTrimText(true);//delete \t and newline and space

            XMLWriter writer = new XMLWriter(out);

            writer.write(doc);

            // close
            out.close();
            writer.close();

        } catch (Exception e) {
            // exception settle down
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * search a singer user by condition
     *
     * @param userType the type of the user you want to search
     * @param searchCondition the name of the condition you want to search
     * @param searchContent the content of the condition you want to search
     * @return the correct userDO
     */
    public static UserDO findSingerNode(String userType, String searchCondition, String searchContent) {
        try {
            // init the reader
            SAXReader reader = new SAXReader();
            // get the Document
            File xmlFile = new File(UserTypeEnum.getPos(UserTypeEnum.getType(userType)));

            Document doc = reader.read(xmlFile);

            //Prepare the xpath
            //Use "//" to be the header indicates that there is no deep constraint,
            //so you can query the child elements in the document
            //[] is called the predicate, is the query condition
            //@id represents the id attribute
            String xpath = "//" + userType + "[@" + searchCondition + "='" + searchContent + "']";

            //search
            Element userEle = (Element) doc.selectSingleNode(xpath);
            if (userEle == null) {
                return null;
            }

            UserDO user = null;
            //convert the element to a userDO
            if(userType == UserTypeEnum.getName(0)){
                user = new MemberDO();
            }
            else if(userType == UserTypeEnum.getName(1)){
                user = new TrainerDO();
            }
            else if(userType == UserTypeEnum.getName(2)){
                user = new AdminDO();
            }
            else if(userType == UserTypeEnum.getName(3)){
                user = new PromoterDO();
            }

            //get attributes
            user.setId(userEle.attributeValue("id"));

            return user;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * search a singer user by condition
     *
     * @param userType the type of the user you want to search
     * @param searchCondition the name of the condition you want to search
     * @param searchContent the content of the condition you want to search
     * @return the correct userDOs
     */
    public static List<UserDO> findNodes(String userType, String searchCondition, String searchContent){
        try {
            // init the reader
            SAXReader reader = new SAXReader();

            int type = UserTypeEnum.getType(userType);

            // get the Document
            File xmlFile = new File(UserTypeEnum.getPos(type));

            Document doc = reader.read(xmlFile);

            String xpath = "//" + userType + "[@" + searchCondition + "='" + searchContent + "']";

            //search
            List<Element> iniResult = doc.selectNodes(xpath);
            List<UserDO> finalResult = new ArrayList<>();


            for(Element user : iniResult){
                UserDO temp = null;
                switch(type){
                    case 0:
                        temp = new MemberDO();
                        ((MemberDO) temp).setType(user.attributeValue("type"));
                        ((MemberDO) temp).setEmail(user.attributeValue("email"));
                        break;
                    case 1:
                        temp = new TrainerDO();
                        break;
                    case 2:
                        temp = new AdminDO();
                        break;
                    case 3:
                        temp = new PromoterDO();
                        break;
                }
                temp.setId(user.attributeValue("id"));
                temp.setPassword(user.attributeValue("password"));
                temp.setName(user.attributeValue("name"));
                temp.setPhoneNumber(user.attributeValue("phoneNumber"));
                temp.setInfo(user.attributeValue("info"));
                finalResult.add(temp);
            }
            return finalResult;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MemberDO m1 = new MemberDO();
        MemberDO m2 = new MemberDO();
        m1.setId("Test001");
        m2.setId("Test002");
        m1.setType("1");
        m2.setType("1");
        addUser(m1);
        addUser(m2);
        List result = findNodes("member", "type", "1");
    }
}
