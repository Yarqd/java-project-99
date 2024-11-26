package hexlet.code.dto;

public final class TaskStatusUpdateDto {

    private String name;
    private String slug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean hasUpdates() {
        return name != null || slug != null;
    }

    @Override
    public String toString() {
        return "TaskStatusUpdateDto{"
                + "name='" + name + '\''
                + ", slug='" + slug + '\''
                + '}';
    }
}
