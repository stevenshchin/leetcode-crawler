package io.injectorstud.leetcodecrawler;

/**
 * It represents an item (ex. a company or a tag) in the sidebar
 */
class SidebarItem {
  private String title;
  private String link;

  SidebarItem(String title, String link) {
    this.title = title;
    this.link = link;
  }

  String getTitle() {
    return title;
  }

  String getLink() {
    return link;
  }

  @Override
  public String toString() {
    return "SidebarItem{" +
        "title='" + title + '\'' +
        ", link='" + link + '\'' +
        '}';
  }
}
