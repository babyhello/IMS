package com.example.yujhaochen.ims;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class IssueFile_Item {

    String Image;

    String Voice;

    String Video;

    String ID;

    public IssueFile_Item(String Image,String Voice,String Video,String ID)
    {
        this.Image = Image;

        this.Voice = Voice;

        this.Video = Video;

        this.ID = ID;

    }

    public String GetImage()
    {
        return this.Image;
    }

    public String GetVoice()
    {
        return this.Voice;
    }

    public String GetVideo()
    {
        return this.Video;
    }

    public String GetID()
    {
        return this.ID;
    }
}
