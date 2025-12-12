package ltts.com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ltts.com.dto.ApiResponse;
import ltts.com.dto.CategoryDto;
import ltts.com.service.CategoryService;



//@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController 
{

	@Autowired
	private CategoryService categoryService;
	
//	@PreAuthorize(value="ROLE_ADMIN")
	@PostMapping("/create-category")
	public ResponseEntity createCategory(@RequestBody CategoryDto categoryDto) {
    	try {
    		if(categoryDto.getName()==null)
        		return ResponseEntity.status(404).body("Name is Required");
        	boolean b=categoryService.findByName(categoryDto.getName());
        	if(b==true)
        		return ResponseEntity.status(200).body("Category Already Exisits");
        	else
        	{
        		categoryDto=categoryService.createCategory(categoryDto);
        	     	return ResponseEntity.status(201).body(new ApiResponse(
        	                true,
        	                "New category created",
        	                categoryDto
        	            ));
        	}
    	}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "Error in creation", null));
    	}

    }
//	@PreAuthorize(value="ROLE_ADMIN")
	@PutMapping("/update-category/{id}")
	public ResponseEntity updateCategory(@PathVariable("id") Long id,@RequestBody CategoryDto categoryDto) {
    	try {
    		if(categoryDto.getName()==null)
        		return ResponseEntity.status(404).body("Name is Required");
    		categoryDto=categoryService.updateCategory(id,categoryDto);
    		return ResponseEntity.ok(new ApiResponse(true, "Category updated successfully", categoryDto));
    	}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "Error WHile Updating", null));
    	}
    }
	@GetMapping("/get-category")
	public ResponseEntity getAllCategories() {
		try {
			List<CategoryDto>ca=categoryService.getAllCategories();
			return ResponseEntity.ok(new ApiResponse(true, "All Categories List", ca));
		}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "Error while getting all categories", null));
    	}
    }
	@GetMapping("/single-category/{slug}")
	public ResponseEntity findBySlug(@PathVariable("slug") String slug) {
		try {
			CategoryDto categoryDto=categoryService.findBySlug(slug);
			return ResponseEntity.ok(new ApiResponse(true, "Get SIngle Category SUccessfully", categoryDto));
		}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "Error While getting Single Category", null));
    	}
    }
//	@PreAuthorize(value="ROLE_ADMIN")
	@DeleteMapping("/delete-category/{id}")
	public ResponseEntity deleteCategory(@PathVariable("id") Long id) {
		try {
			boolean b=categoryService.findByIdGet(id);
			if(b==true) {
				b=categoryService.deleteCategory(id);
				return ResponseEntity.ok(new ApiResponse(true, "Categry Deleted Successfully", null));
			}
			else
			{
				return ResponseEntity.status(500).body("Error in Deleted..");
			}
		}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "error while deleting category", null));
    	}
    }
	
}
