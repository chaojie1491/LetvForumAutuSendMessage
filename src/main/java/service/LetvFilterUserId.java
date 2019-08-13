package service;

public class LetvFilterUserId implements UserLinkFilter {
    @Override
    public String filterLink(String link) {
        return link.substring(link.lastIndexOf("-")+1,link.lastIndexOf("."));
    }
}
