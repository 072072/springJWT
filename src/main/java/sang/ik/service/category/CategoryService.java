package sang.ik.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sang.ik.entity.category.Category;
import sang.ik.exception.CategoryNotFoundException;
import sang.ik.repository.category.CategoryRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> readAll(){
        List<Category> categories = categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();
        return CategoryDto.toDtoList(categories);
    }

    @Transactional
    public void create(CategoryCreateRequest req){
        categoryRepository.save(CategoryCreateRequest.toEntity(req, categoryRepository));
    }

    @Transactional
    public void delete(Long id) {
        if(notExistsCategory(id)) throw new CategoryNotFoundException();
        categoryRepository.deleteById(id);
    }

    private boolean notExistsCategory(Long id) {
        return !categoryRepository.existsById(id);
    }
}
