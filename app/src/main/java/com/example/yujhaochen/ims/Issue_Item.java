package com.example.yujhaochen.ims;

public class Issue_Item {

    String ID;

    String ProjectName;

    String Subject;

    String Date;

    String Priority;

    String WorkNoteCount;

    public Issue_Item(String ID,String ProjectName,String Subject,String Date,String Priority,String WorkNoteCount)
    {
       this.ID = ID;

        this.ProjectName = ProjectName;

        this.Subject = Subject;

        this.Date = Date;

        this.Priority = Priority;

        this.WorkNoteCount = WorkNoteCount;

    }

    public String GetID()
    {
        return this.ID;
    }

    public String GetProjectName()
    {
        return this.ProjectName;
    }

    public String GetSubject()
    {
        return this.Subject;
    }

    public String GetDate()
    {
        return this.Date;
    }

    public String GetPriority()
    {
        return this.Priority;
    }

    public String GetWorkNoteCount()
    {
        return this.WorkNoteCount;
    }
}