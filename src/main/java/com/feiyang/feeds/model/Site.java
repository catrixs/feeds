package com.feiyang.feeds.model;

public class Site {
	private String site;
	private String name;

	public Site(String site, String name) {
		super();
		this.site = site;
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Site)) {
			return false;
		}
		Site other = (Site) obj;
		if (site == null) {
			if (other.site != null) {
				return false;
			}
		} else if (!site.equals(other.site)) {
			return false;
		}
		return true;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Site [site=");
		builder.append(site);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
