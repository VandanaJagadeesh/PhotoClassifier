
package apps;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class PhotoClassifier {
	private static final String RECORD_DELIMITER = "\n";
	private static final String FIELD_DELIMITER = ",";
	private static final String EXTENSION_DELIMITER = "\\.";
	private static final AtomicInteger count = new AtomicInteger(0);
	private Map<String, TreeSet<Photo>> photoByCity;

	public PhotoClassifier() {
		photoByCity = new HashMap<>();
	}

	/*
	 * this method is responsible for putting all functionalities together,
	 * extraction and transformation of photos
	 */
	public String solution(String S) {
		if (S == null || S.isEmpty())
			return "";
		parseAndLoad(S);
		formatPhotosName();
		return getRenamedPhotoNames();
	}

	// parse photo information to get individual photo
	private void parseAndLoad(String s) {
		String[] records = s.split(RECORD_DELIMITER);
		for (String record : records) {
			Photo photo = getParsedPhoto(record);
			addToMap(photo);
		}
	}

	// get each photo info
	private Photo getParsedPhoto(String record) {
		String[] recordInfo = record.split(FIELD_DELIMITER);
		String[] imageInfo = recordInfo[0].split(EXTENSION_DELIMITER);
		return buildPhoto(recordInfo, imageInfo);
	}

	// construct photo object
	private Photo buildPhoto(String[] photoInfo, String[] imageInfo) {
		Photo photo = new Photo();
		photo.sequence = count.getAndIncrement();
		photo.name = imageInfo[0];
		photo.extension = imageInfo[1];
		photo.city = photoInfo[1];
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		photo.date = LocalDateTime.parse(photoInfo[2].trim(), formatter);
		return photo;
	}

	// construct map of city with photo info
	public void addToMap(Photo photo) {
		if (!photoByCity.containsKey(photo.city)) {
			TreeSet<Photo> photos = new TreeSet<>(new InCityPhotoComparator());
			photos.add(photo);
			photoByCity.put(photo.city, photos);
		} else {
			photoByCity.get(photo.city).add(photo);
		}
	}

	// left pad photo names based on number of photos per city
	public void formatPhotosName() {
		for (Entry<String, TreeSet<Photo>> entry : photoByCity.entrySet()) {
			int count = 1;
			int photosLen = String.valueOf(entry.getValue().size()).length();
			for (Photo photo : entry.getValue()) {
				photo.id = String.format("%0" + photosLen + "d", count++);
			}
		}
	}

	//retrieves all photos with transformed names
	public String getRenamedPhotoNames() {
		Collection<TreeSet<Photo>> orderedPhotos = photoByCity.values();
		TreeSet<Photo> photoSet = new TreeSet<>(new SequenceComparator());
		StringBuilder builder = new StringBuilder();
		for (TreeSet<Photo> photos : orderedPhotos) {
			photoSet.addAll(photos);
		}
		for (Photo photo : photoSet) {
			builder.append(photo.getTransformedName()).append(RECORD_DELIMITER);
		}
		return builder.toString();
	}

	// photo class with all it's attributes
	private class Photo {
		private String name;
		private String extension;
		private String city;
		private LocalDateTime date;
		private int sequence;
		private String id;

		public String getTransformedName() {
			StringBuilder builder = new StringBuilder();
			builder.append(city).append(id).append(".").append(extension);
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((city == null) ? 0 : city.hashCode());
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			result = prime * result + ((extension == null) ? 0 : extension.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + sequence;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Photo other = (Photo) obj;
			if (city == null) {
				if (other.city != null)
					return false;
			} else if (!city.equals(other.city))
				return false;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			if (extension == null) {
				if (other.extension != null)
					return false;
			} else if (!extension.equals(other.extension))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (sequence != other.sequence)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Photo [name=" + name + ", extension=" + extension + ", city=" + city + ", date=" + date
					+ ", sequence=" + sequence + "]";
		}

	}

	//comparator to order photos by date
	static class InCityPhotoComparator implements Comparator<Photo> {
		@Override
		public int compare(Photo p1, Photo p2) {
			int result = p1.date.compareTo(p2.date);
			return result == 0 ? p1.sequence - p2.sequence : result;
		}
	}

	//comparator to order photos by increasing sequence number
	static class SequenceComparator implements Comparator<Photo> {
		@Override
		public int compare(Photo p1, Photo p2) {
			return p1.sequence - p2.sequence;
		}
	}

	public static void main(String[] args) {
		String s = "photo.jpg, Warsaw, 2013-09-05 14:08:15\n" + "john.jpg, London, 2015-06-21 15:12:22\n"
				+ "photo2.jpg, Warsaw, 2013-09-05 11:08:15\n" + "photo1.jpg, Warsaw, 2013-09-05 11:08:15\n"
				+ "photo3.jpg, Warsaw, 2013-09-05 11:08:15\n" + "photo10.jpg, Warsaw, 2013-09-05 11:08:15\n"
				+ "photo4.jpg, Warsaw, 2013-09-05 11:08:15\n" + "photo11.jpg, Warsaw, 2013-09-05 11:08:15\n"
				+ "photo5.jpg, Warsaw, 2013-09-05 11:08:17\n" + "photo23.jpeg, Warsaw, 2013-09-05 11:08:15\n"
				+ "photo7.jpg, Warsaw, 2013-09-05 11:08:15\n" + "photo10.jpg, Warsaw, 2013-09-05 11:08:15\n"
				+ "photo9.png, Warsaw, 2013-09-05 11:08:15\n";
		PhotoClassifier photoClassifier = new PhotoClassifier();
		System.out.println(photoClassifier.solution(s));
	}
}
