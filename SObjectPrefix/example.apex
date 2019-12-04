public class SchemaGlobalDescribe{

   //system.debug('Prifix–> ' + customObject__c.sobjecttype.getDescribe().getKeyPrefix());

   public static String findIdPrefixFromObjectName(String sObjectName){
      Map<String, Schema.SObjectType> m  = Schema.getGlobalDescribe() ;
      Schema.SObjectType s = m.get(sObjectName) ;
      Schema.DescribeSObjectResult r = s.getDescribe() ;
      String keyPrefix = r.getKeyPrefix();
      return keyPrefix;

   
   }


    public static String findObjectNameFromRecordIdPrefix(String recordIdOrPrefix){
        String objectName = '';
        try{
            String myIdPrefix = String.valueOf(recordIdOrPrefix).substring(0,3);
            Map<String, Schema.SObjectType> gd =  Schema.getGlobalDescribe(); 
            for(Schema.SObjectType stype : gd.values()){
                Schema.DescribeSObjectResult r = stype.getDescribe();
                String prefix = r.getKeyPrefix();
                System.debug('Prefix is ' + prefix);
                 
                if(prefix!=null && prefix.equals(myIdPrefix)){
                    objectName = r.getName();
                    System.debug('Object Name! ' + objectName);
                    break;
                }
            }
        }catch(Exception e){
            System.debug(e);
        }
        return objectName;
    }
    
    
    
}
