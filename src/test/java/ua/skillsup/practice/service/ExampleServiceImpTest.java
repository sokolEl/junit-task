package ua.skillsup.practice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ua.skillsup.practice.entity.IdGenerator;
import ua.skillsup.practice.entity.Time;
import ua.skillsup.practice.dao.ExampleDao;
import ua.skillsup.practice.entity.ExampleEntity;
import ua.skillsup.practice.exception.ExampleNetworkException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class ExampleServiceImpTest {
    private ExampleService service;
    private ExampleServiceImp serviceImp;
    @Mock
    private ExampleDao exampleDao;

    @Mock
    private Time time;

    @Mock
    private IdGenerator idGenerator;



    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.service = new ExampleServiceImp(exampleDao, time, idGenerator);
        this.serviceImp=new ExampleServiceImp(exampleDao,time,idGenerator);
    }


    @DisplayName("Happy path")
    @Test
    void addNewItem() {

        //Given
        final String title = "test";
        final long expectedId =idGenerator.nextId();
        final Instant expectedDate = Instant.now();
        final BigDecimal testPrice = new BigDecimal("140.09");
        final ExampleEntity expectedEntity = new ExampleEntity(expectedId, title, expectedDate, testPrice);
        when(time.now()).thenReturn(expectedDate);
        when(idGenerator.nextId()).thenReturn(expectedId);
        //When
        service.addNewItem(title,testPrice);
        //Then
        Mockito.verify(exampleDao, times(1)).store(eq(expectedEntity));
    }


   @Test
    public void testException() {
        //Given
       when(exampleDao.findAll()).thenThrow(new ExampleNetworkException());
       assertThrows(ExampleNetworkException.class, () -> service.getStatistic());
    }

    @Test
    public void testGetStatistic() {
        //Given
        //List to mock
        //Mock dao to return list when findAll() was called
        //Prepare expected map with statistic
        Map<Instant, BigDecimal> expected = new HashMap<>();
        List<ExampleEntity> mockedList = new ArrayList<>();
        when(exampleDao.findAll()).thenReturn(mockedList);

        //When
        Map<Instant, BigDecimal> actual = service.getStatistic();

        //Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void isNameEmpty() {
        //Given
        final String expectedTitle = "     ";
        final BigDecimal expectedPrice = new BigDecimal("140.09");
        //Then
        assertThrows(IllegalArgumentException.class, () ->
        service.addNewItem(expectedTitle, expectedPrice));
    }

    @Test
    public void isNameUnique() {  //not convinced
        //Given
        final String expectedTitle = "unicorns";
        final BigDecimal expectedPrice = new BigDecimal("140.09");
        //When
        when(exampleDao.findAll()).thenThrow(new IllegalArgumentException());
        //Then
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }
    @Test
     void isLengthNameCorrect() {
        //Given
        final String expectedTitle1 = "un";
        final BigDecimal expectedPrice1 = new BigDecimal("140.09");
        final String expectedTitle2 = "unicornsunicornsunicorns";
        final BigDecimal expectedPrice2 = new BigDecimal("140.09");
        //Then
        assertThrows(IllegalArgumentException.class, () ->
                service.addNewItem(expectedTitle1, expectedPrice1));
        assertThrows(IllegalArgumentException.class, () ->
                service.addNewItem(expectedTitle2, expectedPrice2));
    }

    @Test
     void isDecimalFormat() {
        final BigDecimal actual=serviceImp.decimalFormat(new BigDecimal("140.009"));
        final BigDecimal expected=new BigDecimal("140.01");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void lowestLimit() {
        //Given
        final String title = "unicorns";
        final BigDecimal price = new BigDecimal("1");
        //Then
        assertThrows(IllegalArgumentException.class, () ->
                service.addNewItem(title, price));
    }
    }

