package ltts.com.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ltts.com.dto.CategoryDto;
import ltts.com.model.Category;

import ltts.com.repository.CategoryRepo;

@Service
public class CategoryServiceImpl implements CategoryService
{

	@Autowired
	private CategoryRepo categoryRepo;
	@Autowired
	private ModelMapper modelMapper;

	private String slugify(String input) {
        // A simple example of a slugify function
        return input.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
	@Override
	public CategoryDto createCategory(CategoryDto categoryDto) {
		Category category=modelMapper.map(categoryDto,Category.class);
		category.setSlug(slugify(category.getName()));
		category=categoryRepo.save(category);
		if(category!=null)
		{
			categoryDto=modelMapper.map(category, CategoryDto.class);
			return categoryDto;
		}
		return null;
	}

	@Override
	public CategoryDto updateCategory(Long id,CategoryDto categoryDto) {
        
		Optional<Category> category=categoryRepo.findById(id);
		Category ca=category.get();
	    ca.setName(categoryDto.getName());
	    ca.setSlug(slugify(ca.getName()));
	    ca=categoryRepo.save(ca);
	    if(ca!=null)
		{
			categoryDto=modelMapper.map(ca, CategoryDto.class);
			return categoryDto;
		}
		return null;
	}

	@Override
	public List<CategoryDto> getAllCategories() {
		List<Category>ca=categoryRepo.findAll();
		return ca.stream().map(
				task -> modelMapper.map(task, CategoryDto.class)).collect(Collectors.toList());
	}

	@Override
	public CategoryDto findBySlug(String slug) {
		Category ca=categoryRepo.findBySlug(slug);
		CategoryDto categoryDto=modelMapper.map(ca, CategoryDto.class);
		return categoryDto;
	}

	@Override
	public boolean deleteCategory(Long id) {
		Optional<Category> category=categoryRepo.findById(id);
		Category ca=category.get();
		if(ca!=null) {
		categoryRepo.deleteById(id);
		return true;}
		else
			return false;
	}

	@Override
	public boolean findByName(String name) {
		Category category=categoryRepo.findByName(name);
		if(category!=null)
			return true;
		else
			return false;
	}
	@Override
	public boolean findBySlugName(String slug) {
		Category ca=categoryRepo.findBySlug(slug);
		if(ca!=null) return true;
		return false;
	}
	@Override
	public boolean findByIdGet(Long id) {
		Optional<Category> category=categoryRepo.findById(id);
		Category ca=category.get();
		if(ca!=null) return true;
		return false;
	}
}
