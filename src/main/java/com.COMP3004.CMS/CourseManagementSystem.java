package com.COMP3004.CMS;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.List;


@Controller
@ComponentScan("com")
public class CourseManagementSystem {

    User userLoggedIn;
    UserCreateFactory factory = new User();

    static String registrationstartDate = "2021-03-31";
    static String registrationTerm1 = "2021-09-20";
    static String registrationTerm2 = "2022-01-20";
    static String registrationTerm3 = "2022-05-20";
    static String WDNgradeStartTerm1 = "2021-03-31";
    static String WDNgradeStartTerm2 = "2022-01-31";
    static String WDNgradeStartTerm3 = "2022-05-31";
    static String withdrawByDateTerm1 = "2021-12-10";
    static String withdrawByDateTerm2 = "2022-04-10";
    static String withdrawByDateTerm3 = "2022-08-10";

    @Autowired
    private UserDatabase repository;

    @Autowired
    private CourseDatabase Courserepository;
    @Autowired
    private DeliverableDatabase Deliverablerepository;


    @Autowired
    private DeliverableDatabase dRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    DatabaseHandler handler;

    //just need user(name) and denied courses temporarily stored
    ArrayList<User> deniedRegistrations = new ArrayList<User>();
    ArrayList<User> deniedWithdrawals = new ArrayList<User>();

    //default constructor
    public CourseManagementSystem() {
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        //sends to dashboard if the user is already logged in
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in"))) {
            return "forward:/dashboard";

        } else {
            return "forward:/login";
        }
    }

    @GetMapping("/login")
    public String login(@ModelAttribute User user, Model model) {
        model.addAttribute("user", new User());
        return "users/login";
    }


    @GetMapping("/createAccount")
    public String createAccount() {
        return "users/create-account";
    }


    @PostMapping("/createAccount")
    public String createAccountHandler(@RequestParam String userType) {
        if (userType.equals("professor")) {
            return "users/professor-create";
        } else if (userType.equals("student")) {
            return "users/student-create";
        } else {
            System.out.println(userType);
            return "error";
        }
    }

    @GetMapping("/approveUser")
    public String approveUser(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            List<User> users = repository.findByActiveIsFalse(); //looks for users who have applied to be created
            model.addAttribute("users", users);
            return "users/user-approve";
        }
        //returns error if there are no users that can be approved
        return "error";

    }

    //submits all deliverable grades
    @GetMapping("/submitAllDel")
    public String submitAllDel(String del, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Deliverable deliverable = dRepository.findByownerAndName(username, del);
        Hashtable<String, Hashtable<Integer, Hashtable<String, Integer>>> submissions = new Hashtable<>();
        Hashtable<Integer, Hashtable<String, Integer>> users = new Hashtable<>();
        Course course = Courserepository.findByCourseCode(deliverable.courseCode);
        for (String student : course.students) {
            Hashtable<String, Integer> subs = new Hashtable<String, Integer>();
            //makes sure we grab the right submission assigned to the student
            if (deliverable.submissions.containsKey(student)) {
                String grade = (String) deliverable.submissions.get(student).get("grade");
                subs.put((String) deliverable.submissions.get(student).get("submissionLink"), Integer.parseInt(grade));
            } else {
                subs.put("No submission exists", 0);
            }
            users.put(repository.findByUsername(student).id, subs);
        }
        submissions.put(deliverable.name, users);
        model.addAttribute("submissions", submissions);
        return "grades/submit-all-del";
    }
    //same as the above but handles the case for only one grading happening
    public String submitAllDel(String del, String student, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Deliverable deliverable = dRepository.findByownerAndName(username, del);
        Hashtable<String, Hashtable<Integer, Hashtable<String, Integer>>> submissions = new Hashtable<>();
        Hashtable<Integer, Hashtable<String, Integer>> users = new Hashtable<>();
        Course course = Courserepository.findByCourseCode(deliverable.courseCode);
        Hashtable<String, Integer> subs = new Hashtable<String, Integer>();
        if (deliverable.submissions.containsKey(student)) {
            String grade = (String) deliverable.submissions.get(student).get("grade");
            subs.put((String) deliverable.submissions.get(student).get("submissionLink"), Integer.parseInt(grade));
        } else {
            subs.put("No submission exists", 0);
        }
        users.put(repository.findByUsername(student).id, subs);
        submissions.put(deliverable.name, users);
        model.addAttribute("submissions", submissions);
        return "grades/submit-all-del";
    }

    @PostMapping("/submitAllDel")
    public String approveAllDel(@RequestParam int[] quantity, @RequestParam String[] usernum, @RequestParam String[] del, @RequestParam String[] subs, HttpSession session) {
        HashMap<String, HashMap> submissions = new HashMap<String, HashMap>();
        String username = (String) session.getAttribute("username");
        String deliv = del[0];
        Integer[] usernumbers = new Integer[usernum.length];
        int i = 0;
        //grabs each user from the form
        for (String str : usernum) {
            usernumbers[i] = Integer.parseInt(str);
            i++;
        }
        Deliverable deliverable = dRepository.findByownerAndName(username, deliv);
        //updates the deliverable grade for each student
        for (int j = 0; j < usernum.length; j += 1) {
            User user = repository.findByid(usernumbers[j]);
            deliverable.updateSubmission(user.username, subs[j], Integer.toString(quantity[j]));
        }
        dRepository.save(deliverable);
        return "grades/submit-successful";
    }

    //handles selecting the deliverable to grade
    @GetMapping("/selectDel")
    public String selectDel(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        List<String> deliverables = new ArrayList<String>();
        for (Deliverable del : dRepository.findByowner(username)) {
            deliverables.add(del.name);
        }
        model.addAttribute("deliverables", deliverables);
        return "grades/select-del-all";
    }

    @PostMapping("/selectDel")
    public String selectedDel(@RequestParam String deliverable, Model model, HttpSession session) {
        return submitAllDel(deliverable, model, session);
    }

    //handles selecting the deliverable to grade
    @GetMapping("/selectOneDel")
    public String selectOneDel(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        List<String> deliverables = new ArrayList<String>();
        for (Deliverable del : dRepository.findByowner(username)) {
            deliverables.add(del.name);
        }
        model.addAttribute("deliverables", deliverables);
        return "grades/select-del-one";
    }

    @PostMapping("/selectOneDel")
    public String selectedOneDel(@RequestParam String deliverable, Model model, HttpSession session) {
        return selectUserDel(deliverable, model, session);
    }

    //handles selecting the user to grade
    @GetMapping("/selectUserDel")
    public String selectUserDel(String deliverable, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        List<String> students = Courserepository.findByCourseCode(dRepository.findByownerAndName(username, deliverable).courseCode).getStudents();
        model.addAttribute("deliverable", deliverable);
        model.addAttribute("users", students);
        return "grades/select-del-user";
    }

    @PostMapping("/selectUserDel")
    public String selectedUSerDel(@RequestParam String deliverable, @RequestParam String user, Model model, HttpSession session) {
        return submitAllDel(deliverable, user, model, session);
    }

    //submits all the deliverable grades
    @GetMapping("/submitAllCourse")
    public String submitAllCourse(String crs, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Hashtable<String, Hashtable<Integer, Hashtable<Integer, Integer>>> courses = new Hashtable<>();
        Hashtable<Integer, Hashtable<Integer, Integer>> users = new Hashtable<>();
        Course course = Courserepository.findByCourseCode(crs);
        List<Deliverable> deliverable = dRepository.findByCourseCode(crs);

        for (String student : course.students) {
            Hashtable<Integer, Integer> subs = new Hashtable<Integer, Integer>();
            int grade = 0;
            for (Deliverable del : deliverable) {
                //grabs the existing grade from all combined deliverable grades
                if (del.submissions.containsKey(student)) {
                    String grd = (String) del.submissions.get(student).get("grade");
                    int weight = del.weighting;
                    grade += (Integer.parseInt(grd) * weight) / 100;
                }
                User user = repository.findByUsername(student);
                //adds the current course grade with the combined deliverable grades
                if (user.getGrades().containsKey(crs)) {
                    subs.put(grade, Integer.parseInt(user.getGrades().get(crs)));
                } else {
                    subs.put(grade, 0);
                }

            }
            users.put(repository.findByUsername(student).id, subs);
        }
        courses.put(crs, users);
        model.addAttribute("courses", courses);
        return "grades/submit-all-course";
    }
    //same as above but for only processing one user
    public String submitAllCourse(String crs, String student, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Hashtable<String, Hashtable<Integer, Hashtable<Integer, Integer>>> courses = new Hashtable<>();
        Hashtable<Integer, Hashtable<Integer, Integer>> users = new Hashtable<>();
        Course course = Courserepository.findByCourseCode(crs);
        List<Deliverable> deliverable = dRepository.findByCourseCode(crs);
        Hashtable<Integer, Integer> subs = new Hashtable<Integer, Integer>();
        int grade = 0;
        for (Deliverable del : deliverable) {
            //grabs the existing grade from all combined deliverable grades
            if (del.submissions.containsKey(student)) {
                String grd = (String) del.submissions.get(student).get("grade");
                int weight = del.weighting;
                grade += (Integer.parseInt(grd) * weight) / 100;
            }
            User user = repository.findByUsername(student);
            //adds the current course grade with the combined deliverable grades
            if (user.getGrades().containsKey(crs)) {
                subs.put(grade, Integer.parseInt(user.getGrades().get(crs)));
            } else {
                subs.put(grade, 0);
            }
        }
        users.put(repository.findByUsername(student).id, subs);

        courses.put(crs, users);
        model.addAttribute("courses", courses);
        return "grades/submit-all-course";
    }

    @PostMapping("/submitAllCourse")
    public String approveAllCourse(@RequestParam int[] quantity, @RequestParam String[] usernum, @RequestParam String[] crs, @RequestParam String[] subs, HttpSession session) {
        String username = (String) session.getAttribute("username");
        String course = crs[0];
        Integer[] usernumbers = new Integer[usernum.length];
        int i = 0;
        //gets every user from the form
        for (String str : usernum) {
            usernumbers[i] = Integer.parseInt(str);
            i++;
        }
        //updates every final course grade for each student
        for (int j = 0; j < usernum.length; j += 1) {
            User.Student user = (User.Student) repository.findByid(usernumbers[j]);
            user.grading(course, Integer.toString(quantity[j]));
            repository.save(user);
        }
        return "grades/submit-successful";
    }

    //handles selecting the course to grade
    @GetMapping("/selectCourse")
    public String selectCourse(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        List<String> courses = new ArrayList<String>();
        for (Course course : Courserepository.findByProfessor(username)) {
            courses.add(course.courseCode);
        }
        model.addAttribute("courses", courses);
        return "grades/select-course-all";
    }

    @PostMapping("/selectCourse")
    public String selectedCourse(@RequestParam String course, Model model, HttpSession session) {
        return submitAllCourse(course, model, session);
    }

    //handles selecting the course to grade
    @GetMapping("/selectOneCourse")
    public String selectOneCourse(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        List<String> courses = new ArrayList<String>();
        for (Course course : Courserepository.findByProfessor(username)) {
            courses.add(course.courseCode);
        }
        model.addAttribute("courses", courses);
        return "grades/select-course-one";
    }

    @PostMapping("/selectOneCourse")
    public String selectedOneCourse(@RequestParam String course, Model model, HttpSession session) {
        return selectUserCourse(course, model, session);
    }

    //handles selecting the user to grade
    @GetMapping("/selectUserCourse")
    public String selectUserCourse(String course, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        List<String> students = Courserepository.findByCourseCode(course).getStudents();
        model.addAttribute("course", course);
        model.addAttribute("users", students);
        return "grades/select-course-user";
    }

    @PostMapping("/selectUserCourse")
    public String selectedUSerCourse(@RequestParam String course, @RequestParam String user, Model model, HttpSession session) {
        return submitAllCourse(course, user, model, session);
    }


    @PostMapping("/approveUser")
    public String approveUserHandling(@RequestParam(value = "usernameChecked", required = false) List<String> users, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            if (users != null) {
                for (String user : users) {
                    User x = repository.findByUsername(user);
                    x.setActive(true);
                    repository.save(x);
                }
            }
            return "redirect:/approveUser";
        }
        return "error";
    }

    //handles getting the stats exported the the excel
    @GetMapping(value = "/getStats")
    public String getStats(HttpServletResponse response, HttpSession session) throws IOException {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            Stats s = new Stats(repository);
            XSSFWorkbook file = s.getStats();
            String filename = "Export.xlsx";
            String filetype = "xlsx";

            response.addHeader("Content-disposition", "attachment;filename=" + filename);
            response.setContentType(filetype);
            file.write(response.getOutputStream());
            response.flushBuffer();
            return null;
        }
        return "error";
    }

    //provides a list of all users so that admin can choose one to delete
    @GetMapping("/deleteUser")
    public String deleteUser(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            List<User> users = repository.findAll();
            model.addAttribute("users", users);
            return "users/user-delete";
        }
        return "error";
    }

    //deletes a user
    @PostMapping("/deleteUser")
    public String deleteUserHandling(@RequestParam(value = "usernameChecked", required = false) List<String> users, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            if (users != null) {
                for (String user : users) {
                    User x = repository.findByUsername(user);
                    ArrayList<String> courses = x.getCourseList();
                    for (String courseID : courses) {
                        if (courseID != null) {
                            handler.deregister_student(user, courseID);
                        }
                    }
                    repository.deleteByUsername(user);
                }
            }
            return "redirect:/deleteUser";
        }
        return "error";
    }

    //handles request to create a user
    @PostMapping("/createUserRequest")
    public String createUserRequest(@RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam String firstname,
                                    @RequestParam String lastname,
                                    @RequestParam(required = false) String date,
                                    @RequestParam(required = false) String gender) {
        if (repository.findByUsername(username) != null && date == null) {
            return "users/professor-create-taken";
        } else if (repository.findByUsername(username) != null) {
            return "users/student-create-taken";
        }

        if (date == null) {
            repository.save(factory.createUser(username, password, "Professor", repository.findTopByOrderByIdDesc().getId() + 1, date, gender, firstname, lastname));
        } else {
            repository.save(factory.createUser(username, password, "Student", repository.findTopByOrderByIdDesc().getId() + 1, date, gender, firstname, lastname));
        }
        return "users/create-successful";
    }

    @GetMapping("/changePassword")
    public String changePassword() {
        return "users/forgot-password";
    }

    //changes the user's password
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String username,
                                 @RequestParam String password) {
        if (repository.findByUsername(username) == null) {
            return "users/forgot-password-error";
        } else {
            User x = repository.findByUsername(username);
            x.setPassword(password);
            repository.save(x);
            return "users/change-successful";
        }
    }

    //logs the user in
    @PostMapping("/login")
    public Object loginhandler(@ModelAttribute User user, Model model, HttpSession session) {
        model.addAttribute(user);

        // authenitcation
        User loginUser = repository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (loginUser != null && loginUser.getActive()) {
            session.setAttribute("username", loginUser.getUsername());
            session.setAttribute("role", loginUser.getRole());
            session.setAttribute("logged_in", true);
            return "redirect:/dashboard";
        } else {
            return "users/login";
        }
    }

    //returns the user dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        System.out.println(session.getAttribute("logged_in"));
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in"))) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);

            //decides which homepage should be returned depending on the user
            if (user.getRole().equals("Admin")) {
                return "homepages/admin-home";
            } else if (user.getRole().equals("Professor")) {
                return "homepages/professor-home";
            } else if (user.getRole().equals("Student")) {
                ArrayList<String> coursecodes = user.getCourseList();
                ArrayList<Course> courses = new ArrayList<Course>();
                ArrayList<String> wdnCourses = user.getPrevCourses();
                for (String coursecode : coursecodes) {
                    courses.add(Courserepository.findByCourseCode((String) coursecode));
                }
                model.addAttribute("courses", courses);
                return "homepages/student-home";
            } else {
                return "error";
            }
        } else {
            System.out.println("Why are we here");
            return "users/login";
        }
    }

    //serves the right dashboard immediately after login
    @PostMapping("/dashboard")
    public String dashboardhandler(@ModelAttribute User user, Model model, HttpSession session) {

        user = (User) session.getAttribute("user");

        if (repository.findByUsernameAndPassword(user.getUsername(), user.getPassword()) != null) {
            if (repository.findByUsernameAndRole(user.getUsername(), "Admin") != null) {
                return "homepages/admin-home";
            } else if (repository.findByUsernameAndRole(user.getUsername(), "Professor") != null) {
                return "homepages/professor-home";
            } else if (repository.findByUsernameAndRole(user.getUsername(), "Student") != null) {
                return "homepages/student-home";
            } else {
                return "error";
            }
        } else {
            return "users/login";
        }
    }

    //brings up the deliverable submit page
    @GetMapping("/submitDeliverables")
    public String submitDeliverables(@RequestParam(name = "CID") String CourseId, Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in"))) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);
            System.out.println("The student here is: " + user.getUsername());
            System.out.println("The course in question here is: " + CourseId);

            //Getting required deliverables
            List<Deliverable> courseDeliverables = dRepository.findByCourseCode(CourseId);
            List<String> deliverableNames = new ArrayList<String>();

            for (Deliverable courseDeliverable : courseDeliverables) {
                deliverableNames.add(courseDeliverable.name);
            }

            model.addAttribute("deliverableNames", deliverableNames);
            model.addAttribute("targetCourse", Courserepository.findByCourseCode(CourseId));
            model.addAttribute("user", repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role")));

            return "deliverables/submit-deliverable";
        }
        return "error";
    }

    //brings up information relevant to courses
    @GetMapping("/courseInformation")
    public String courseInformation(@RequestParam(name = "CourseID") String CourseId, Model model, HttpSession session) {

        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in"))) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);
            Course c = Courserepository.findByCourseCode(CourseId);
            ArrayList<String> tempDeliverablesIDs = c.getDeliverables();
            ArrayList<String> tempDeliverables = new ArrayList<String>();

            for (String id : tempDeliverablesIDs) {
                tempDeliverables.add(Deliverablerepository.findDeliverableByDeliverableID(id).name);
            }

            model.addAttribute("deliverables", tempDeliverables);
            model.addAttribute("course", c);
            return "courses/student-course-info";
        }
        return "error";
    }

    //brings up the page that allows for deliverable creation
    @GetMapping("/createDeliverable")
    public String createDeliverable(Model model, HttpSession session) {

        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in") && session.getAttribute("role").equals("Professor"))) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);

            if (user.getUsername() != null) {
                System.out.println("REACHED DELIVERABLE CREATION PAGE");
                //Getting required info here
                List<String> assignedCourses = ((User.Professor) user).retrieveCourses();
                System.out.println("Assigned Courses: " + assignedCourses.toString());
                model.addAttribute("assignedCourses", assignedCourses);
                model.addAttribute("user", repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role")));
                return "deliverables/create-deliverable";
            } else {
                System.out.println("Reached getUsername = null error");
                return "error";
            }
        }
        System.out.println("Reached not logged in error page");
        return "error";
    }

    //brings up the page to choose which deliverable to delete
    @GetMapping("/deleteDeliverable")
    public String deleteDeliverable(Model model, HttpSession session) {

        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Professor")) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);
            System.out.println("REACHED DELIVERABLE DELETION PAGE");

            //Getting prof's deliverables
            List<Deliverable> profDeliverables = dRepository.findByowner(user.username);
            List<String> deliverableNames = new ArrayList<>();

            for (Deliverable profDeliverable : profDeliverables) {
                deliverableNames.add(profDeliverable.name);
            }

            System.out.println("Prof's deliverables: " + deliverableNames.toString());
            model.addAttribute("deliverableNames", deliverableNames);
            model.addAttribute("user", repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role")));

            return "deliverables/delete-deliverable";
        }

        return "error";
    }

    //brings up the page to choose which deliverable to modify
    @GetMapping("/modifyDeliverable")
    public String modifyDeliverable(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Professor")) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);
            System.out.println("REACHED DELIVERABLE MODIFICATION PAGE");

            List<Deliverable> profDeliverables = dRepository.findByowner(user.username);
            List<String> deliverableNames = new ArrayList<>();

            for (Deliverable profDeliverable : profDeliverables) {
                deliverableNames.add(profDeliverable.name);
            }

            System.out.println("Prof's deliverables: " + deliverableNames.toString());
            model.addAttribute("deliverableNames", deliverableNames);
            model.addAttribute("user", repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role")));

            return "deliverables/modify-deliverable";
        }

        return "error";
    }

    //brings up the page to choose which course description to delete
    @GetMapping("/courseDescriptionUpdate")
    public String courseDescriptionUpdate(@ModelAttribute("User") User user, Model model, HttpSession session) {
        if (userLoggedIn != null) {
            user = userLoggedIn;
        }
        user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
        System.out.println("===========================> USER IS:");
        System.out.println(user);
        model.addAttribute("user", user);
        User.Professor tempUser;
        if (user.getRole().equals("Professor"))
            tempUser = (User.Professor) user;
        else
            return "error";
        ArrayList<String> courses = tempUser.retrieveCourses();
        model.addAttribute("courses", courses);

        return "courses/update-course-info";
    }

    //updates course info for a course
    @PostMapping("/updateCourseInfo")
    public String updateCourseInfo(@RequestParam String courseCode,
                                   @RequestParam String courseInfo) {
        if (Courserepository.findByCourseCode(courseCode) == null) {
            System.out.println("Course Info Update: Course submitted doesn't exist");
            return "courses/update-course-info-error";
        }

        System.out.println("Updating course info for " + courseCode);
        handler.update_courseinfo("Professor", courseCode, courseInfo);

        return "courses/course-info-update-successful";
    }

    //brings up the page to help create a course
    @GetMapping("/createCourse")
    public String createCourse(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Professor")) {

            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);

            List<User> professors = repository.findByRole("Professor");
            model.addAttribute("professors", professors);

            return "courses/create-course";
        } else {
            return "error";
        }
    }

    //creates the course
    @PostMapping("/createCourseRequest")
    public String createCourseRequest(@RequestParam String courseName,
                                      @RequestParam String courseCode,
                                      @RequestParam int courseLevel,
                                      @RequestParam int courseNumber,
                                      @RequestParam String professor,
                                      @RequestParam String courseDept,
                                      @RequestParam String courseInfo,
                                      @RequestParam int startTerm,
                                      @RequestParam int endTerm, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            System.out.println("Received new course's data, Code: " + courseCode + ", Name: " + courseName);
            if (Courserepository.findByCourseCode(courseCode) != null) {
                System.out.println("Submitted course code exists");
                return "courses/create-course-exists";
            }

            System.out.println("Saving new course " + courseName);
            if (courseLevel != 0 || courseNumber != 0 || courseDept != null) {
                String registerBy, wdnStart, withdrawBy;
                if (startTerm == 1) {
                    registerBy = registrationTerm1;
                    wdnStart = WDNgradeStartTerm1;
                } else if (startTerm == 2) {
                    registerBy = registrationTerm2;
                    wdnStart = WDNgradeStartTerm2;
                } else {
                    registerBy = registrationTerm3;
                    wdnStart = WDNgradeStartTerm3;
                }

                if (endTerm == 1)
                    withdrawBy = withdrawByDateTerm1;
                else if (endTerm == 2)
                    withdrawBy = withdrawByDateTerm2;
                else
                    withdrawBy = withdrawByDateTerm3;

                Course tempCourse = new Course(courseName, courseCode, courseLevel, courseNumber, courseDept);
                tempCourse.setRegisterByDate(registerBy);
                tempCourse.setWDNgradeStartDate(withdrawBy);
                tempCourse.setWithdrawByDate(withdrawBy);
                tempCourse.setCourseInfo(courseInfo);
                Courserepository.save(tempCourse);

                System.out.println("Assigning professor with username " + professor);
                handler.assign_prof(professor, courseCode);
            } else
                handler.add_course("Admin", courseCode);

            return "courses/course-create-successful";
        }
        return "error";
    }

    //brings up a page to select a course to delete
    @GetMapping("/deleteCourse")
    public String deleteCourse(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);
            List<Course> courses = Courserepository.findAll();
            model.addAttribute("courses", courses);

            return "courses/delete-course";
        } else {
            return "error";
        }
    }

    //deletes a course
    @PostMapping("/deleteCourseRequest")
    public String deleteCourseRequest(@RequestParam String courseCode, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            if (Courserepository.findByCourseCode(courseCode) == null) {
                System.out.println("Trying to delete course that does not exist");
                return "courses/delete-course-error";
            }

            System.out.println("Deleting " + courseCode);
            handler.delete_course("Admin", courseCode);

            return "courses/course-delete-successful";
        }
        return "error";
    }

    /*
    This is a function to test Login using jmeter.
    It exists because ajax does not send json in a way that @requestBody will parse
    and jmeter cannot send Json in a way that @modelattribute can parse
     */
    @PostMapping("/loginTest")
    public String logintesthandler(@RequestBody User user, Model model, HttpSession session) {
        model.addAttribute(user);

        // authentication
        User loginUser = repository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (loginUser != null && loginUser.getActive()) {
            session.setAttribute("username", loginUser.getUsername());
            session.setAttribute("role", loginUser.getRole());
            session.setAttribute("logged_in", true);
            return "redirect:/dashboard";
        } else {
            // figure out message to send on fail and how to send it to the html
            System.out.println("We have failed the login test");
            return "users/login";
        }
    }

    //logs a user out
    @PostMapping("/logout")
    public String logouthandler(@ModelAttribute User user, Model model, HttpSession session) {

        session.setAttribute("logged_in", false);
        return "users/login";
    }

    //jmeter testing
    @GetMapping(value = "/dashboardTest")
    public String dashboardTest(Model model) {
        System.out.println("We are in the DashboardTest mapping function");
        String[] coursenames = {"Comp3000", "Comp3001", "Comp3002", "Comp3003", "Comp3004"};
        String[] courselinks = {"courses/Comp3000", "courses/Comp3001", "courses/Comp3002", "courses/Comp3003", "courses/Comp3004"};

        model.addAttribute("courses", coursenames);
        model.addAttribute("links", courselinks);
        model.addAttribute("user", factory.createUser("Sepehr", "Password423", "Student", 7, "null", "null", "Dave", "Ian"));

        return "dashboard";
    }

    //brings up a page for users to register
    @GetMapping("/Cregister")
    public String CourseRegisterPage(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Student")) {
            //System.out.println("REACHED COURSE REGISTER PAGE");

            List<String> Depts = mongoTemplate.findDistinct("courseDept", Course.class, String.class);
            List<Integer> levels = mongoTemplate.findDistinct("courselevel", Course.class, Integer.class);

            //System.out.println("Depts: " + Depts.toString());
            //System.out.println("levels: " + levels.toString());

            model.addAttribute("Depts", Depts);
            model.addAttribute("levels", levels);

            model.addAttribute("user", repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role")));

            return "courses/CourseReg";
        } else {
            return "error";
        }
    }

    //gets courses
    @GetMapping("/getCourses")
    @ResponseBody
    public List<Course> FindCourses(@RequestParam Map<String, String> allParams, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in"))) {
            //System.out.println("Parameters are " + allParams.entrySet());
           // System.out.println("We are in the get course function");
            List<Course> courses = new ArrayList<Course>();
            if (allParams.containsKey("CourseID")) {
                courses.add(Courserepository.findByCourseCode(allParams.get("CourseID")));
            } else if (allParams.containsKey("CourseLevel") && allParams.containsKey("Subject")) {
                courses.addAll(Courserepository.findByCourselevelAndCourseDept(Integer.parseInt(allParams.get("CourseLevel")), allParams.get("Subject")));
            } else if (allParams.containsKey("CourseNum") && allParams.containsKey("Subject")) {
                courses.addAll(Courserepository.findByCoursenumberAndCourseDept(Integer.parseInt(allParams.get("CourseNum")), allParams.get("Subject")));
            } else if (allParams.containsKey("CourseNum")) {
                courses.addAll(Courserepository.findBycoursenumber(Integer.parseInt(allParams.get("CourseNum"))));
            } else if (allParams.containsKey("Subject")) {
                courses.addAll(Courserepository.findByCourseDept(allParams.get("Subject")));
            } else if (allParams.containsKey("CourseLevel")) {
                courses.addAll(Courserepository.findByCourselevel(Integer.parseInt(allParams.get("CourseLevel"))));
            } else {
                courses.addAll(Courserepository.findAll());
            }


            System.out.println(courses);
            return courses;
        }
        return null;
    }

    //registers a user to a course
    @RequestMapping(method = RequestMethod.POST, value = "/Courseregistration")
    @ResponseBody
    public String CourseRegistration(@RequestBody JSONObject courses, Model model, HttpSession session) throws ParseException {
        //System.out.println("We are registering the student");
        Object[] keys = courses.keySet().toArray();

        Date courseRegisterStart = new SimpleDateFormat("yyyy-MM-dd").parse(registrationstartDate);
        Date courseRegisterEnd;
        Date now = new Date();
        String currentUsername = (String) session.getAttribute("username");

        try {
            List<String> tempList = new ArrayList<String>();
            for (int i = 0; i < keys.length; ++i) {
                User tempUser = new User();
                String courseID = (String) courses.get(keys[i]);
                String registerByDate = Courserepository.findByCourseCode(courseID).getRegisterByDate();
                courseRegisterEnd = new SimpleDateFormat("yyyy-MM-dd").parse(registerByDate);
                if (repository.findByUsername(currentUsername).getCourseList().contains(courseID)) {
                    System.out.println(courseID + "is already in student's courseList");
                } else if (now.after(courseRegisterStart) && now.before(courseRegisterEnd)) {
                    System.out.println(courseID + " added, valid registration");
                    tempList.add(courseID);
                    handler.register_student(currentUsername, courseID);
                } else {
                    System.out.println(courseID + " is invalid registration");
                    tempUser = repository.findByUsername(currentUsername);
                    //empty course list for tempUser to keep track of denied registrations
                    tempUser.setCourseList(new ArrayList<>());
                    tempUser.courseList.add(courseID);
                    deniedRegistrations.add(tempUser);
                }
            }
        } catch (NullPointerException e) {
        }


        //System.out.println("Courses registered");
        return "redirect:/Cregister";
    }

    @GetMapping("/dropCourse")
    public String dropCourse(Model model, HttpSession session) {

        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Student")) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);

            User.Student tempUser;
            if (user.getRole().equals("Student"))
                tempUser = (User.Student) user;
            else
                return "error";

            List<String> courses = tempUser.retrieveCourses();
            model.addAttribute("courses", courses);

            return "courses/student-drop-course";
        } else {
            return "error";
        }
    }

    //drops a course for a student
    @PostMapping("/dropCourseRequest")
    public String dropCourseRequest(HttpSession session, @RequestParam String courseCode) throws ParseException {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Student")) {
            String currentUsername = (String) session.getAttribute("username");
            User tempUser = repository.findByUsername(currentUsername);
            tempUser.setCourseList(new ArrayList<>());
            String wdnStartStr = Courserepository.findByCourseCode(courseCode).getWDNgradeStartDate();
            Date wdnStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(wdnStartStr);
            String withdrawByStr = Courserepository.findByCourseCode(courseCode).getWithdrawByDate();
            Date withdrawByDate = new SimpleDateFormat("yyyy-MM-dd").parse(withdrawByStr);
            Date now = new Date();

            if (now.after(withdrawByDate)) {
                System.out.println("Past withdrawal period");
                tempUser.courseList.add(courseCode);
                deniedWithdrawals.add(tempUser);
                return "courses/invalid-withdraw-request";
            } else if (now.after(wdnStartDate)) {
                System.out.println("Dropping " + courseCode + " for student with WDN");
                handler.deregister_student_WDN(currentUsername, courseCode);
                return "courses/course-withdraw-successful";
            } else {
                System.out.println("Dropping " + courseCode + " for student with no grade");
                handler.deregister_student(currentUsername, courseCode);
                return "courses/course-withdraw-successful";
            }
        }
        return "error";
    }

    //brings up late registration request page
    @GetMapping("/lateRegistrationRequests")
    public String lateRegistrationRequests(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            if (deniedRegistrations.isEmpty())
//            model.addAttribute("users", new ArrayList<User>());
                return "courses/denied-registrations-empty";
            else
                model.addAttribute("users", deniedRegistrations);
            return "courses/late-registrations";
        }
        return "error";
    }

    //processes late registration request
    @PostMapping("/lateRegistrationRequests")
    public String lateRegistrationRequestsHandling(@RequestParam(value = "usernameChecked", required = false) List<String> users, HttpSession session) {
//        System.out.println("Denied registrations list =============> " + deniedRegistrations);
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            if (users != null) {
                for (String user : users) {
                    if (!deniedRegistrations.isEmpty()) {
                        for (User searchTerm : deniedRegistrations) {
                            if (searchTerm.getUsername().equals(user)) {
                                deniedRegistrations.remove(searchTerm);
                                if (deniedRegistrations.isEmpty()) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return "redirect:/lateRegistrationRequests";
        } else {
            return "admin";
        }
    }

    //brings up page for late withdrawals
    @GetMapping("/lateWithdrawalRequests")
    public String lateWithdrawalRequests(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            if (deniedWithdrawals.isEmpty())
                return "courses/denied-withdrawals-empty";
            else
                model.addAttribute("users", deniedWithdrawals);
            return "courses/late-withdrawals";
        } else {
            return "error";
        }
    }

    //processes late withdrawal request
    @PostMapping("/lateWithdrawalRequests")
    public String lateWithdrawalRequestsHandling(@RequestParam(value = "usernameChecked", required = false) List<String> users, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Admin")) {
            if (users != null) {
                for (String user : users) {
                    if (!deniedWithdrawals.isEmpty()) {
                        for (User searchTerm : deniedWithdrawals) {
                            if (searchTerm.getUsername().equals(user)) {
                                deniedWithdrawals.remove(searchTerm);
                                if (deniedWithdrawals.isEmpty()) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return "redirect:/lateWithdrawalRequests";
        } else {
            return "error";
        }
    }

    //submits a deliverable
    @RequestMapping(method = RequestMethod.POST, value = "/deliverableSubmission")
    @ResponseBody
    public void deliverableSubmit(@RequestBody JSONObject dObject, Model model, HttpSession session) {
        System.out.println("Adding the new deliverable entry...");

        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Professor")) {
            //Creating new deliverable
            //Getting required parameters
            String dID = UUID.randomUUID().toString().replace("-", "");
            String course = dObject.get("course").toString();
            String name = dObject.get("name").toString();
            String description = dObject.get("desc").toString();
            int weight = Integer.parseInt(dObject.get("weighting").toString());
            int daysDue = Integer.parseInt(dObject.get("daysDue").toString());

            System.out.println("Deliverable details: ");
            System.out.println("DID: " + dID);
            System.out.println("Course: " + course);
            System.out.println("Name: " + name);
            System.out.println("Description: " + description);
            System.out.println("Weight: " + weight);
            System.out.println("Days due: " + daysDue);

            handler.Add_deliverable(session.getAttribute("username").toString(), course, dID);


            //Retrieving newly created deliverable then passing params
            Deliverable d = dRepository.findDeliverableByDeliverableID(dID);
            d.setName(name);
            d.setDetails(description);
            d.setWeighting(weight);
            d.setDueDate(daysDue);

            //Saving instance to server
            dRepository.save(d);

            System.out.print("Deliverable Entry Added - \nName of Deliverable: ");
            System.out.println(dRepository.findDeliverableByDeliverableID(dID).name);
            System.out.println("Owner (Username): " + d.owner);
        }

    }

    //deletes a deliverable
    @RequestMapping(method = RequestMethod.POST, value = "/deliverableDeletion")
    @ResponseBody
    public String deliverableDelete(@RequestBody JSONObject dObject, Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Professor")) {
            System.out.println("Deleting the target deliverable...");

            //Finding deliverable to delete
            try {
                String dName = dObject.get("dName").toString();
                Deliverable d = dRepository.findByownerAndName(session.getAttribute("username").toString(), dName);
                handler.remove_deliverable("Professor", d.courseCode, d.deliverableID);
            } catch (NullPointerException e) {
                return "That professor is not authorized to change that deliverable";
            }
            System.out.println("Deleted.");
            return "sucsessfully deleted deliverable";
        } else {
            return "error";
        }
    }

    //modifies a deliverable
    @RequestMapping(method = RequestMethod.POST, value = "/deliverableModification")
    @ResponseBody
    public void deliverableModify(@RequestBody JSONObject dObject, Model model, HttpSession session) {
        System.out.println("Editing the target deliverable...");
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Professor")) {
            try {
                //Finding target deliverable
                String oldName = dObject.get("oldName").toString();
                Deliverable targetD = dRepository.findByownerAndName(session.getAttribute("username").toString(), oldName);

                //Parsing post and updating deliverable
                String newName = dObject.get("newName").toString();
                String newDescription = dObject.get("newDescription").toString();
                int newWeight = Integer.parseInt(dObject.get("newWeight").toString());
                int newDueDate = Integer.parseInt(dObject.get("newDueDate").toString());

                targetD.setName(newName);
                targetD.setDetails(newDescription);
                targetD.setWeighting(newWeight);
                targetD.setDueDate(newDueDate);

                //Saving instance to server
                dRepository.save(targetD);

                System.out.println("Target update successful");
            } catch (NullPointerException e) {
                System.out.println("Target update unsuccessful");
            }
        }
    }

    //student deliverable submission
    @RequestMapping(method = RequestMethod.POST, value = "/deliverableStudentSubmission")
    @ResponseBody
    public void deliverableStudentSubmit(@RequestBody JSONObject dObject, Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) && session.getAttribute("role").equals("Student")) {
            System.out.println("Sending deliverable submission...");

            //Finding the deliverable
            Deliverable targetDeliverable = dRepository.findByCourseCodeAndName(dObject.get("targetCourse").toString(), dObject.get("dName").toString());

            //Updating deliverable submissions
            targetDeliverable.addNewSubmission(session.getAttribute("username").toString(), dObject.get("subLink").toString());
            dRepository.save(targetDeliverable);

            System.out.println("Submission complete");
        }
    }

    //brings up the student audit page
    @GetMapping("/studentAudit")
    public String getAudit(Model model, HttpSession session) {

        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in"))) {

            //initilizing table values
            ArrayList<String> courseLables = new ArrayList<String>();
            ArrayList<String> courses = new ArrayList<String>();
            ArrayList<String> terms = new ArrayList<String>();
            ArrayList<String> courseGrades = new ArrayList<String>();
            ArrayList<String> CreditValues = new ArrayList<String>();
            ArrayList<String> comments = new ArrayList<String>();

            //fetching user information
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            Hashtable<String, String> grades = user.getGrades();
            courses.addAll(user.getCourseList()); // gets list of current courses
            courses.addAll(user.getPrevCourses()); // gets list of previously taken courses

            //populating the values of the table
            for (String cours : courses) {
                Course c = Courserepository.findByCourseCode(cours);
                if (grades.containsKey(cours)) {
                    courseLables.add(c.courseName + ":  " + c.courseName);
                    CreditValues.add("0.5");
                    comments.add(" ");
                    terms.add(c.getTerm());
                    if (!(grades.get(cours).equals("WDN"))) {
                        courseGrades.add(GradeVisitor.visit(grades.get(cours)));
                    } else {
                        courseGrades.add(grades.get(cours));
                    }
                } else {
                    courseLables.add(c.courseDept + ":  " + c.courseName);
                    CreditValues.add("0.5");
                    comments.add(" ");
                    terms.add(c.getTerm());
                    courseGrades.add("CUR");
                }
            }
            //populating table with courses that were not linked properly
            if (courses.size() == 0 && grades.size() != 0) {
                Iterator it = grades.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry mapElement = (Map.Entry) it.next();
                    courses.add((String) mapElement.getKey());
                    try {
                        Course c = Courserepository.findByCourseCode((String) mapElement.getKey());
                        terms.add(c.getTerm());
                        courseLables.add(c.courseName + ":  " + c.courseName);
                    } catch (NullPointerException e) {
                        terms.add("unknown to database");
                        courseLables.add("unknown to database");
                    }
                    CreditValues.add("0.5");
                    comments.add(" ");
                    courseGrades.add((String) mapElement.getValue());
                }
            }

            //adding attributes for thymleaf
            model.addAttribute("courses", courses.toArray());
            model.addAttribute("Terms", terms.toArray());
            model.addAttribute("Titles", courseLables.toArray());
            model.addAttribute("Creditvalues", CreditValues.toArray());
            model.addAttribute("Grades", courseGrades.toArray());
            model.addAttribute("Comments", comments.toArray());
            return "users/audit";
        } else {
            return "error";
        }
    }
}