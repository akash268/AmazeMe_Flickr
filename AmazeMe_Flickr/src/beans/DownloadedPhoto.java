package beans;

public class DownloadedPhoto {
	Photo photo;
	String filePath;
	public DownloadedPhoto() {
		// TODO Auto-generated constructor stub
	}
	public Photo getPhoto() {
		return photo;
	}
	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public DownloadedPhoto(Photo photo, String filePath) {
		super();
		this.photo = photo;
		this.filePath = filePath;
	}
}
