package ltts.com.service;


import java.util.List;

import ltts.com.dto.CategoryDto;

public interface CategoryService 
{

	public CategoryDto createCategory(CategoryDto categoryDto);
	public CategoryDto updateCategory(Long id,CategoryDto categoryDto);
	public List<CategoryDto> getAllCategories();
	public CategoryDto findBySlug(String slug);
	public boolean deleteCategory(Long id);
	public boolean findByName(String name);
	public boolean findBySlugName(String slug);
	public boolean findByIdGet(Long id);
}
