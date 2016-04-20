package demo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.util.FileCopyUtils;

@SpringBootApplication
//@EnableContextResourceLoader
public class CloudS3Application {

    private static final String FOLDER_SUFFIX = "/";
    @Value("${cloud.aws.s3.bucket}")
	private String bucket;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Autowired
    private AmazonS3Client amazonS3Client;



	@PostConstruct
	public void resourceAccess() throws IOException {
		String location = "s3://" + bucket + "/file.txt";

        // write the file
        FileInputStream inputStream = new FileInputStream("/home/richard/workspace/workspace_spring/Spring_Boot/livelessons-cloud/livelessons-cloud-s3/test.txt");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, "test.txt", inputStream , new ObjectMetadata());
        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);
//        IOUtils.closeQuietly(inputStream);

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, "test.txt" );
        System.out.println(FileCopyUtils.copyToString(new InputStreamReader(amazonS3Client.getObject(getObjectRequest).getObjectContent())));

        listFiles();

	}
    
    public void listFiles(){
        List<String> result = new ArrayList<String>();
        String path = "";
        ObjectListing objList = amazonS3Client.listObjects(bucket, "");
        for (S3ObjectSummary summary:objList.getObjectSummaries()) {
            //ignore folders
            if(! summary.getKey().endsWith(FOLDER_SUFFIX)){
                result.add(summary.getKey().substring(path.length()));
            }
        }
        System.out.println("Found "  + result.size() + " objects");
    }

	public static void main(String[] args) {
		SpringApplication.run(CloudS3Application.class, args);
	}

}
