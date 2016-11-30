package com.example.yujhaochen.ims;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class Spec_Item {

    String ComponentName;

    String ComponentContent;


    public Spec_Item( String ComponentName,String ComponentContent)
    {
        this.ComponentName = ComponentName;

        this.ComponentContent = ComponentContent;


    }

    public String GetComponentName()
    {
        return this.ComponentName;
    }

    public String GetComponentContent()
    {
        return this.ComponentContent;
    }

}
