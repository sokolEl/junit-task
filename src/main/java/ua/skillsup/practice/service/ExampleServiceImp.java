package ua.skillsup.practice.service;
import ua.skillsup.practice.entity.IdGenerator;
import ua.skillsup.practice.entity.Time;
import ua.skillsup.practice.dao.ExampleDao;
import ua.skillsup.practice.entity.ExampleEntity;
import ua.skillsup.practice.exception.ExampleNetworkException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.time.Instant;

public class ExampleServiceImp implements ExampleService{

    private final Time time;
    private final IdGenerator idGenerator;
    private final ExampleDao exampleDao ;

    public ExampleServiceImp(ExampleDao exampleDao, Time time, IdGenerator idGenerator) {
        this.time=time;
        this.idGenerator=idGenerator;
        this.exampleDao = exampleDao;
    }

    @Override
    public void addNewItem(String title, BigDecimal price)  {
        isNameEmpty(title,"Empty string");
        isNameUnique(title,"Name should be unique");
        isLengthNameCorrect(title,"Available length range 3-20");
        decimalFormat(price);
        lowestLimit(price,"Lowest limit is at 15.00");
        Instant date = time.now();
        long id = idGenerator.nextId();
        ExampleEntity exampleEntity=new ExampleEntity(id,title,date,price);
        try {
            exampleDao.store(exampleEntity);
            System.out.println("Success!");
        } catch (ExampleNetworkException e) {
            e.getMessage();
        }
    }

    @Override
    public Map < Instant, BigDecimal> getStatistic() {

        List<ExampleEntity> currentEntity = exampleDao.findAll();


        Map<Instant, BigDecimal> staff = new HashMap<>();


        for (int i = 0; i < currentEntity.size() ; i++) {
            List<BigDecimal> values = new ArrayList<>();
            Instant tmpDate = currentEntity.get(i).getDateIn();

            for (int j = 0; j < currentEntity.size(); j++) {
                if (currentEntity.get(j).getDateIn().equals(tmpDate)) {
                    values.add(currentEntity.get(j).getPrice());
                }
            }
            BigDecimal  finalPrice=average(values);

            staff.put(tmpDate, finalPrice);
        }
        return staff;
    }

    public BigDecimal average(List<BigDecimal> val) throws ArithmeticException {

        if(val.isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = new BigDecimal("0");
        int count = 0;

        for (BigDecimal aVal : val) {
            sum = sum.add(aVal);
            count++;
        }
        if(count!=0){
            return sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);

        }
        else return sum;
    }


    private void isNameEmpty(String value, String errorMessage) {
        if (value.trim().length() == 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
    private  void isNameUnique(String name, String errorMessage) {

        List<ExampleEntity> currentEntity = exampleDao.findAll();

        for (int i = 0; i < currentEntity.size(); i++) {
        ExampleEntity findName = currentEntity.get(i);
        if (findName.getTitle().equals(name)){
            throw new IllegalArgumentException(errorMessage);
        }
    }
    }

    private void isLengthNameCorrect(String name,String errorMessage) {
        if (name.length()<3 || name.length() >20) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


    public BigDecimal decimalFormat(BigDecimal tmpValue) {
        if(tmpValue.scale()>=3){
            BigDecimal value  = tmpValue.setScale(2, RoundingMode.HALF_UP);
           // System.out.println("Number was rounded");
            return value;
        }
        else return tmpValue;
    }

    private void lowestLimit(BigDecimal tmpValue,String errorMessage) {
        BigDecimal lowest=new BigDecimal(15);
        if (tmpValue.compareTo(lowest)==-1 ) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

}
