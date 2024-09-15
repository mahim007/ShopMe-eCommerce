package com.mahim.shopme.admin.paging;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

@Getter
public class PagingAndSortingHelper {

    private final ModelAndViewContainer model;
    private final String listName;
    private final String moduleURL;
    private final String sortField;
    private final String sortDir;
    private final String keyword;

    public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String moduleURL, String sortField,
                                  String sortDir, String keyword) {
        this.model = model;
        this.listName = listName;
        this.moduleURL = moduleURL;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyword = keyword;
    }

    public void updateModelAttributes(int pageNo, Page<?> page) {
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        int pageSize = page.getSize();
        int currentPageSize = page.getNumberOfElements();
        int startNo = ((pageNo - 1) * pageSize) + 1;
        int endNo = (startNo + currentPageSize) - 1;

        List<?> items = page.getContent();

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("total", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute(listName, items);
        model.addAttribute("moduleURL", moduleURL);
    }

    public void addAttribute(String name, Object value) {
        model.addAttribute(name, value);
    }

    public Page<?> listEntities(int pageNum, int pageSize, SearchRepository<?, Integer> repository) {
        Sort sort = Sort.by(this.getSortField());
        if (this.getSortField().equals("destination")) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        }
        sort = StringUtils.equals(this.getSortDir(), "asc") ? sort.ascending() : sort.descending();
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        if (this.getKeyword() != null) {
            return repository.findAllByKeyword(this.getKeyword(), pageRequest);
        } else {
            return repository.findAll(pageRequest);
        }
    }
}
