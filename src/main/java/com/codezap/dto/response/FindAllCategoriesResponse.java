package com.codezap.dto.response;

import java.util.List;

public record FindAllCategoriesResponse(
        List<FindCategoryResponse> categories
) {
}
