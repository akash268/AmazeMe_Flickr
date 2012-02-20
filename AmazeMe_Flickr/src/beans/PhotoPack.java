package beans;

import java.util.ArrayList;

public class PhotoPack {
int page;
int pages;
int perpage;
int total;
ArrayList<Photo> photo=new ArrayList<Photo>();
private String stat;
public int getPage() {
	return page;
}
public void setPage(int page) {
	this.page = page;
}
public int getPages() {
	return pages;
}
public void setPages(int pages) {
	this.pages = pages;
}
public int getPerpage() {
	return perpage;
}
public void setPerpage(int perpage) {
	this.perpage = perpage;
}
public int getTotal() {
	return total;
}
public void setTotal(int total) {
	this.total = total;
}
public ArrayList<Photo> getPhoto() {
	return photo;
}
public void setPhoto(ArrayList<Photo> photo) {
	this.photo = photo;
}
public void setStat(String stat) {
	this.stat = stat;
}
public String getStat() {
	return stat;
}
}
