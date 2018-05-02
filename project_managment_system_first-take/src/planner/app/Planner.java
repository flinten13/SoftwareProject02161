package planner.app;


import acceptance_tests.Developer;
import planner.domain.Activity;
import planner.domain.Project;
import planner.domain.User;
import planner.domain.WorkHours;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * The main planner
 */
public class Planner {


    // The active developer of the system
    public User activeUser;

    // All the developers on the system
    public List<User> users =  new ArrayList<>();

    // All the projects on the system
    public List<Project> projects = new ArrayList<>();

    /********************
     *  Authentication  *
     ********************/

    /**
     * Set an active user session for the planner.
     * @param credentials The developers credentials
     * @param password The developers password
     * @throws AuthenticationException If the developers is logged in throw error. If the user us typed in wrong throw error.
     */
    public void userLogIn(String credentials, String password) throws AuthenticationException{

        // Is there a user session?
        // Before anything clear the session
        if(activeSession()){
            activeUser = null;
        }

        // Go through each of the registered Developers and check the password and credentials.
        // If the current one is present set that user as an active user session.
        for (User user : users) {
            if (Objects.equals(user.getCredentials(), credentials) &&
                    Objects.equals(user.getPassword(), password)) {
                //Set the active session
                activeUser = user;
            } else {
                throw new AuthenticationException("Your credentials or password was wrong");
            }
        }

    }

    /**
     * Is there a active session on the system
     * @return boolean. Is there a ongoing session.
     */
    public boolean activeSession(){
        return activeUser != null;
    }

    /**
     * Is there an active session on the system?
     * @throws AuthenticationException Throw error if there isn't an active sessions.
     */
    public void checkSession() throws AuthenticationException {
        if (!activeSession()) {
            throw new AuthenticationException("Login required");
        }
    }

    /**
     * Is there an active admin session on the system.
     * @throws AuthenticationException Throw error if there isn't an active admin sessions.
     */
    public void checkAdminSession() throws AuthenticationException {
        if (activeUser.isAdmin()) {
            throw new AuthenticationException("Administrator required");
        }
    }

    /**
     * Log the active user out of the system (remove active session)
     * @throws OperationNotAllowedException If there is not an active session on the system throw error.
     */
    public void userLogOut() throws OperationNotAllowedException {
        // Remove the active session from the system
        if (activeUser == null){
            throw new OperationNotAllowedException("There is no user logged in to the system");
        }
        activeUser = null;
    }

    /***********************
     *  Planner Operations *
     ***********************/

    /**
     * Add a new user to the system
     * @param credentials Credentials
     * @param password Password
     * @throws OperationNotAllowedException If the session is not a Admin session throw exception.
     *                                      If the developers is in the system throw exception.
     * @throws AuthenticationException If the user is not a admin.
     */
    public void createUser(String credentials, String password) throws OperationNotAllowedException, AuthenticationException{
        checkAdminSession();

        if (getUser(credentials) != null){
            throw new OperationNotAllowedException("Developer is registered");
        }
        users.add(new User(credentials, password));
    }

    /**
     * Delete a given user from the system.
     * @param user
     * @throws OperationNotAllowedException If the user is not on the system throw exception.
     * @throws AuthenticationException If the user is not a admin.
     */
    public void deleteUser(User user)throws OperationNotAllowedException, AuthenticationException{
        checkAdminSession();

        if(!(users.contains(user))){
            throw new OperationNotAllowedException("No user with the given credentials" + user.getCredentials() + "found");
        } else {
            users.remove(user);
        }
    }

    /**
     * Crete a project on the planner.
     * @param project
     * @throws OperationNotAllowedException If the project you are trying to create is on the system
     *                                      There is a project with the same title on the planner
     *                                      The time frame for the project is i
     * @throws AuthenticationException There is not a user on the system to perform this operation.
     */
    public void createProject(Project project) throws OperationNotAllowedException, AuthenticationException  {
        checkSession();

        if(!(projects.contains(project))){
            throw new OperationNotAllowedException("Project is already on the planner");
        } else if(getProject(project.getTitle()) != null){
            throw new OperationNotAllowedException("A project with that title is on the planner");
        } else if (project.getEstimatedEndTime().before(project.getEstimatedStartTime())) {
            throw new OperationNotAllowedException("Invalid time for project");
        } else {
            projects.add(project);
        }

    }

    /**
     * Remove a project form the planner.
     * @param project
     * @throws OperationNotAllowedException The project you are trying to remove is not on the system.
     * @throws AuthenticationException The user is not a admin.
     */
    public void deleteProject(Project project) throws OperationNotAllowedException, AuthenticationException{
        checkAdminSession();

        if(!(projects.contains(project))){
            throw new OperationNotAllowedException("No project with the given title " + project.getTitle() + " was found");
        } else {
            projects.remove(project);
        }
    }

    /**
     * Assign project manager status to a user.
     * @param user A user
     * @param project
     * @throws OperationNotAllowedException the user you are trying to promote is not a part of the system.
     * @throws AuthenticationException If the session is not a Admin session throw exception.
     */
    public void assignProjectManager(User user, Project project) throws OperationNotAllowedException, AuthenticationException{
        checkAdminSession();

        if(!(users.contains(user))){
            throw new OperationNotAllowedException("The user you are trying to promote is not a part of the planner");
        }

        project.setProjectManager(user);
    }

    /**
     *
     * @param project
     * @throws AuthenticationException If the session is not a Admin session throw exception.
     */
    public void removeProjectManager(Project project) throws AuthenticationException{
        checkAdminSession();
        project.setProjectManager(null);
    }
    
    /*********************
     *  User Responders  *
     *********************/

    /**
     * Handle the respond to the user.
     */
    private UserResponse userResponse = new UserResponse();


    /** SEND RESPONE
     *
     */

    private void showAlert(){

    }

    /**************************
     *  Setters and getters   *
     **************************/

    /**
     * Search the developers <LIST>  for a specific developer
     * @param credentials The developers credentials
     * @return Returns the specific found developer
     */
    public User getUser(String credentials) {

        // Set the initial currentDeveloper as null
        User foundUser = null;

        for (User user : users) {
            if (Objects.equals(user.getCredentials(), credentials)){
                foundUser =  user;
            }
        }
        return foundUser;
    }


    /**
     *
     * @param activityStartTime
     * @param activityEndTime
     * @return
     */
    public List<User> getAvailableUsers(Calendar activityStartTime, Calendar activityEndTime) throws Exception {
        checkSession();

        List<User> availableUsers = new ArrayList<>();

        // Check if a given user is available
        for (User user : users) {
            for (WorkHours workHour : user.getWorkHours()) {

                if(user.getWorkHours() == null){
                    availableUsers.add(user);
                }

                if (!(workHour.getStartTime().after(activityStartTime)) && !(workHour.getEndTime().before(activityEndTime))) {
                    if (!(workHour.getStartTime().equals(activityStartTime)) && !(workHour.getEndTime().equals(activityEndTime))) {
                        availableUsers.add(user);
                    }
                } else {
                    break;
                }
            }
        }

        if(availableUsers.isEmpty()){
            return null;
        }

        return availableUsers;
    }

    /**
     * Search the projects <LIST> for a specific project
     * @param title The project title
     * @return Returns the specific found project
     */
    public Project getProject(String title) {

        // Set the initial currentProject as null
        Project currentProject = null;

        for (Project project : projects) {
            if (Objects.equals(project.getTitle(), title)){
                currentProject =  project;
            }
        }
        return currentProject;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public User getActiveUser(User activeUser) {
        return activeUser;
    }

    public User getActiveUser() {
        return activeUser;
    }
}
