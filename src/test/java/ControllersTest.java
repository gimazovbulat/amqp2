import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.itis.producers.Producer;

@WebMvcTest
public class ControllersTest {
    @MockBean
    Producer producer;

//    void testing(){
//        Mockito.when(producer.sendMessage(Mockito.any(), "", ""));
//    }
}
