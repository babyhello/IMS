package com.example.yujhaochen.ims;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class Notification_Item {

    String Author_WorkID;

    String Author_Name;

    String Date;

    String Title;

    String Content;

    String Image;

    String F_Master_ID;

    String Desc;

    public Notification_Item(String Author_WorkID, String Author_Name, String Date, String Title, String Content, String Desc, String Image, String F_Master_ID)
    {
        this.Author_WorkID = Author_WorkID;

        this.Author_Name = Author_Name;

        this.Date = Date;

        this.Title = Title;

        this.Content = Content;

        this.Image = Image;

        this.F_Master_ID = F_Master_ID;

        this.Desc = Desc;
    }

    public String GetAuthor_WorkID()
    {
        return this.Author_WorkID;
    }

    public String GetAuthor_Name()
    {
        return this.Author_Name;
    }

    public String GetDate()
    {
        return this.Date;
    }

    public String GetTitle()
    {
        return this.Title;
    }

    public String GetContent()
    {
        return this.Content;
    }

    public String GetImage()
    {
        return this.Image;
    }

    public String Get_F_Master_ID()
    {
        return this.F_Master_ID;
    }

    public String Get_Desc() {
        return this.Desc;
    }
}
