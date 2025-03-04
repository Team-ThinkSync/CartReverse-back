package com.project.webshopproject.product;

import com.project.webshopproject.category.entity.ProductCategory;
import com.project.webshopproject.category.repository.ProductCategoryRepository;
import com.project.webshopproject.product.dto.ProductAddRequestDto;
import com.project.webshopproject.product.dto.ProductResponseDto;
import com.project.webshopproject.product.dto.ProductFindResponseDto;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.entity.ProductImage;
import com.project.webshopproject.product.repository.ProductImageRepository;
import com.project.webshopproject.product.repository.ProductQueryRepository;
import com.project.webshopproject.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ProductCategoryRepository productCategoryRepository;


    // 전체 상품 조회
    public List<ProductResponseDto> getAllProducts(){
        return productQueryRepository.findAllProducts();
    }
    //카테고리별 조회 api 추가

     // 세부 상품 조회
     public ProductFindResponseDto getProductById(Long productId) {
         return productQueryRepository.findProductById(productId);
     }

    @Value("${file.upload-dir}")
    private String uploadDir; // 이미지 파일 저장 되는 경로

    // 상품 추가
    public void addProduct(ProductAddRequestDto productAddRequestDto, List<MultipartFile> images){
        ProductCategory productCategory = productCategoryRepository.findById(productAddRequestDto.categoryId())
                .orElseThrow(()-> new IllegalArgumentException("카테고리가 존재하지않음"));

        try{
            Product product = Product.builder()
                    .name(productAddRequestDto.productName())
                    .price(productAddRequestDto.productPrice())
                    .stock(productAddRequestDto.productStock())
                    .category(productCategory)
                    .categoryType(productAddRequestDto.categoryType())
                    .build();

            System.out.println(product.getPrice());
            productRepository.save(product);

            List<String> savedImageUrls = saveImage(images);
            List<ProductImage> productImages = new ArrayList<>();
            for(int i = 0; i < images.size(); i++){
                ProductImage productImage = ProductImage.builder()
                        .image(savedImageUrls.get(i))
                        .orderNo(i + 1)
                        .isMain(i == 0)
                        .product(product)
                        .build();
                productImages.add(productImage);
            }
            productImageRepository.saveAll(productImages);

            // 메인 이미지 ID 가져오기 (isMain == true인 첫 번째 이미지)
            ProductImage mainImage = productImages.stream()
                    .filter(ProductImage::getIsMain)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("메인 이미지가 없습니다."));

            // 새로 생성된 상품에 메인 이미지를 설정하여 업데이트
            product = Product.builder()
                    .productId(product.getProductId())  // 기존 productId를 그대로 사용
                    .name(product.getName())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .category(product.getCategory())
                    .categoryType(product.getCategoryType())
                    .mainImage(mainImage)  // 메인 이미지 설정
                    .build();

            // 상품 저장
            productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 이미지를 서버에 저장하고 경로를 반환하는 메서드
    public List<String> saveImage(List<MultipartFile> images) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        List<String> savedImageUrls = new ArrayList<>();

        for(int i = 0; i < images.size(); i++){
            MultipartFile productImg = images.get(i);
            // 파일 이름 생성 (현재 시간 + 원본 파일 이름)
            String currentTime = LocalDateTime.now().format(formatter);
            String fileName = currentTime + "-" + productImg.getOriginalFilename();
            // 저장 경로 생성
            Path filePath = Paths.get(uploadDir, fileName);
            // 경로가 존재하지 않으면 디렉토리 생성
            Files.createDirectories(filePath.getParent());
            // 파일 저장
            Files.copy(productImg.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // 저장된 파일의 상대 경로 반환
            savedImageUrls.add(fileName);
        }
        return savedImageUrls;
    }

    // 상품 수정
//    public ProductEditRequestDto editProduct(Long itemId, ProductEditRequestDto productEditRequestDto, List<MultipartFile> images){
//        try{
//            List<String> savedImageUrls = saveImage(images);
//            productEditRequestDto.setItemImg(savedImageUrls);
//            editItem.updateItems(
//                    productEditRequestDto.getItemName(),
//                    productEditRequestDto.getItemImg().get(0),
//                    productEditRequestDto.getItemPrice(),
//                    productEditRequestDto.getItemStock());
//            //서브 이미지들을 담아야해
//            for(int i = 1; i < savedImageUrls.size(); i++){
//                String subImg = savedImageUrls.get(i);
//                ProductImg productImg = ProductImg.builder()
//                        .items(editItem)
//                        .itemImg(subImg)
//                        .build();
//                productImgRepository.save(productImg);
//            }
//        }
//        catch (Exception e){
//
//        }
//
//        if (images != null ) {
//            try{
//                // 받아온 이미지들 리스트에 담아서 순서대로 1번은 메인,나머지는 서브이미지테이블로
//                List<String> imagePath = saveImage(images);
//                editItem.setItemImg(imagePath);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        Items updatedItem = itemRepository.save(editItem);
//
//        return new ItemEditRequestDto(
//                updatedItem.getItemName(),
//                "/images/" + updatedItem.getItemImg(),
//                updatedItem.getItemPrice(),
//                updatedItem.getItemStock()
//        );
//    }

    // 상품 삭제
    public void deleteProduct(Long productId){
        // 1. 삭제할 상품 조회 (메인 이미지 포함)
        Product deleteItem = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        productQueryRepository.deleteProduct(productId);
        productRepository.delete(deleteItem);
    }

}
