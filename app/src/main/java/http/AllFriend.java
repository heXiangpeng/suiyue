package http;

import java.util.List;

/**
 * Created by hexiangpeng on 15/9/9.
 */
public class AllFriend {
    private List<Friendtype> friend;

    public List<Friendtype> getFriend(){
        return friend;
    }

    public void setFriend(List<Friendtype> friend){
        this.friend=friend;
    }
}
