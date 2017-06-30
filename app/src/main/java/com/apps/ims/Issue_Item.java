package com.apps.ims;

public class Issue_Item {

    String ID;

    String ProjectName;

    String Subject;

    String Date;

    String Priority;

    String WorkNoteCount;

    String Read;

    String Author;

    String IssueStatus;


    public Issue_Item(String ID, String ProjectName, String Subject, String Date, String Priority, String WorkNoteCount, String Read, String Author, String IssueStatus) {
        this.ID = ID;

        this.ProjectName = ProjectName;

        this.Subject = Subject;

        this.Date = Date;

        this.Priority = Priority;

        this.WorkNoteCount = WorkNoteCount;

        this.Read = Read;

        this.Author = Author;

        this.IssueStatus = IssueStatus;

    }

    public String GetID() {
        return this.ID;
    }

    public String GetProjectName() {
        return this.ProjectName;
    }

    public String GetSubject() {
        return this.Subject;
    }

    public String GetDate() {
        return this.Date;
    }

    public String GetPriority() {
        return this.Priority;
    }

    public String GetWorkNoteCount() {
        return this.WorkNoteCount;
    }

    public String GetRead() {
        return this.Read;
    }

    public String GetAuthor() {
        return this.Author;
    }

    public String GetIssueStatus() {
        return this.IssueStatus;
    }
}