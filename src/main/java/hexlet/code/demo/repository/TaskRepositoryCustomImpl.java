package hexlet.code.demo.repository;

import hexlet.code.demo.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Метод для поиска задач с фильтрацией по названию, исполнителю, статусу и метке.
     *
     * @param titleCont часть названия задачи для поиска (может быть null)
     * @param assigneeId идентификатор исполнителя (может быть null)
     * @param status название статуса задачи (может быть null)
     * @param labelId идентификатор метки (может быть null)
     * @return список задач, соответствующих указанным фильтрам
     */
    @Override
    public List<Task> findTasksByFilters(String titleCont, Long assigneeId, String status, Long labelId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = cb.createQuery(Task.class);
        Root<Task> taskRoot = query.from(Task.class);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтр по названию
        if (titleCont != null && !titleCont.isEmpty()) {
            predicates.add(cb.like(cb.lower(taskRoot.get("name")), "%" + titleCont.toLowerCase() + "%"));
        }

        // Фильтр по исполнителю
        if (assigneeId != null) {
            predicates.add(cb.equal(taskRoot.get("assignee").get("id"), assigneeId));
        }

        // Фильтр по статусу задачи
        if (status != null && !status.isEmpty()) {
            predicates.add(cb.equal(taskRoot.get("taskStatus").get("name"), status));
        }

        // Фильтр по метке
        if (labelId != null) {
            predicates.add(cb.isMember(labelId, taskRoot.get("labels")));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }
}
