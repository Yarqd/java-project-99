package hexlet.code.dto;

public final class TaskStatusUpdateDto {

    private String name;
    private String slug;

    /**
     * Возвращает имя статуса задачи.
     * @return имя статуса задачи
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя статуса задачи.
     * @param name имя статуса задачи
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает slug статуса задачи.
     * @return slug статуса задачи
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Устанавливает slug статуса задачи.
     * @param slug slug статуса задачи
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }
}
