package com.apps.ims;

/**
 * Created by androids on 2016/10/21.
 */
public class List_Item {

    String Text;

    String Value;

    Boolean Selected;

    public List_Item(String Text, String Value) {
        this.Text = Text;

        this.Value = Value;

    }

    public String GetText() {
        return this.Text;
    }

    public String GetValue() {
        return this.Value;
    }

    public Boolean GetSelected() {
        return this.Selected;
    }

}
