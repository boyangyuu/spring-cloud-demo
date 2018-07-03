package com.mobileenerlytics.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


//todo api doc : https://spring.io/guides/gs/testing-restdocs/

//todo try put a demo in project, see what happened,

@Document(collection = "Project")
public class Project {
    @Id // https://docs.spring.io/spring-data/data-mongo/docs/1.4.0.M1/reference/html/mapping-chapter.html
//    @Indexed(unique = true)
//    @Field("id")
    // todo 迁移 dev_db to dev_db1, re build String to objectID.
    // todo , query, for (commit) {// find change project; find change branch new cid, pid; find change test new cid }
    // https://stackoverflow.com/questions/48291819/change-mongodb-id-from-string-to-objectid
    private ObjectId _id;

    private String name;

    private String userId;

    private String prefix;

    private String commitUrlPrefix;

    private String tourStatus;

//    private List<ObjectId> demos;

    public Project() {

    }

    public Project(String name, String userId) {
        this.name = name;
        this.userId = userId;
        this.prefix = "origin/release";// default
        this.tourStatus = "true";

    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTourStatus() {
        return tourStatus;
    }

    public void setTourStatus(String tourStatus) {
        this.tourStatus = tourStatus;
    }

//    public static List<Project> queryProjectsByUsername(EntityManager em, String name) throws SystemException, NotSupportedException,
//            HeuristicRollbackException, HeuristicMixedException, RollbackException, TransactionRequiredException {
//        TransactionManager tm = ProHibernateUtil.getInstance().getTransactionManager();
//        if(tm.getStatus() == Status.STATUS_NO_TRANSACTION) throw new TransactionRequiredException("should start TransactionManager first!");
//        TypedQuery<Project> query = em.createQuery("from Project b where b.userId = :userId", Project.class)
//                .setParameter("userId", name);
//        List<Project> projects = query.getResultList();
//        return projects;
//    }

//    public static Project getProject(EntityManager em, String projectName, String userName)
//            throws TransactionRequiredException, SystemException, HeuristicRollbackException, HeuristicMixedException, NotSupportedException, RollbackException {
//        TransactionManager tm = ProHibernateUtil.getInstance().getTransactionManager();
//        if(tm.getStatus() == Status.STATUS_NO_TRANSACTION) throw new TransactionRequiredException("should start TransactionManager first!");
//        TypedQuery<Project> query = em.createQuery("from Project b where b.name = :name and b.userId = :userId", Project.class)
//            .setParameter("name", projectName)
//            .setParameter("userId", userName);
//        Project project = DBOperation.getSingleResult(query);
//        if(project != null)
//            return project;
//        project = new Project(projectName, userName);
//        em.persist(project);
//        return project;
//    }
//
//    public static Project queryProject(EntityManager em, String projectId) throws TransactionRequiredException, SystemException {
//        TransactionManager tm = ProHibernateUtil.getInstance().getTransactionManager();
//        if(tm.getStatus() == Status.STATUS_NO_TRANSACTION) throw new TransactionRequiredException("should start TransactionManager first!");
//        Project project = null;
//        try {
//            TypedQuery<Project> query = em.createQuery("from Project b where b._id = :id", Project.class)
//                    .setParameter("id", projectId);
//            project = query.getSingleResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//            project = null;
//        }
//        return project;
//    }

    public String getCommitUrlPrefix() {
        return commitUrlPrefix;
    }

    public void setCommitUrlPrefix(String commitUrlPrefix) {
        this.commitUrlPrefix = commitUrlPrefix;
    }

//    public void addDemo(ObjectId demo2) {
//        this.demos.add(demo2);
//    }
}
